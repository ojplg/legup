package org.center4racialjustice.legup.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class DaoHelper {

    private static String columnsAsString(List<Column> columnList){
        List<String> columnNames = columnList.stream().map(Column::getName).collect(Collectors.toList());
        return String.join(", ", columnNames);
    }

    private static String insertStatement(String table, List<Column> columnList){
        StringBuilder bldr = new StringBuilder();
        bldr.append("insert into ");
        bldr.append(table);
        bldr.append(" ( ");
        bldr.append(columnsAsString(columnList));
        bldr.append(" ) values ( DEFAULT, ");
        for(int idx=0; idx<columnList.size()-2; idx++){
            bldr.append("?, ");
        }
        bldr.append("? ");
        bldr.append(" ) ");
        bldr.append(" RETURNING ID ");

        return bldr.toString();
    }

    private static <T extends Identifiable> String updateStatement(String table, List<Column> columnList, T item){
        StringBuilder sql = new StringBuilder("update ");
        sql.append(table);
        sql.append(" set ");
        for(int idx=1; idx<columnList.size(); idx++){
            Column column = columnList.get(idx);
            sql.append(column.getName());
            sql.append(" = ? ");
            if (idx < columnList.size() - 1){
                sql.append(", ");
            }
        }
        sql.append(" where id = ");
        sql.append(item.getId());
        sql.append(" RETURNING ID");
        return sql.toString();
    }

    public static <T extends Identifiable> Long doInsert(Connection connection, String table, List<Column> columnList, T item, Map<String, Long> references){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = DaoHelper.insertStatement(table, columnList);

        try {
            preparedStatement = connection.prepareStatement(sql);

            for (int index = 1; index < columnList.size(); index++) {
                Column column = columnList.get(index);
                ColumnType columnType = column.getColumnType();
                switch (columnType) {
                    case String:
                        Function<T, String> stringGetter = column.getGetter();
                        preparedStatement.setString(index, stringGetter.apply(item));
                        break;
                    case Long:
                        Function<T, Long> longGetter = column.getGetter();
                        preparedStatement.setLong(index, longGetter.apply(item));
                        break;
                    case Reference:
                        preparedStatement.setLong(index, references.get(column.getName()));
                        break;
                }

            }

            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong("id");
        } catch (SQLException se){
            throw new RuntimeException("Wrapped SQL exception for " + sql, se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se){
                // TODO: Log here.
            }
        }
    }

    public static <T extends Identifiable> long doUpdate(Connection connection, String table, List<Column> columnList, T item, Map<String, Long> references){
        String sql = DaoHelper.updateStatement(table, columnList, item);

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            for(int idx=1; idx<columnList.size(); idx++){
                Column column = columnList.get(idx);
                ColumnType columnType = column.getColumnType();
                switch (columnType){
                    case String:
                        Function<T, String> stringGetter = column.getGetter();
                        String stringValue = stringGetter.apply(item);
                        preparedStatement.setString(idx, stringValue);
                        break;
                    case Long:
                        Function<T, Long> longGetter = column.getGetter();
                        Long longValue = longGetter.apply(item);
                        preparedStatement.setLong(idx, longValue);
                        break;
                    case Reference:
                        preparedStatement.setLong(idx, references.get(column.getName()));
                        break;
                }
            }
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong("id");
        } catch (SQLException se) {
            throw new RuntimeException(se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se){
                // TODO: Log here.
            }
        }
    }

    public static <T> T populate(ResultSet resultSet, List<Column> columnList, Supplier<T> supplier,
                                 Map<String, Function> finders) throws SQLException {
        T item = supplier.get();

        for (Column column: columnList) {
            String name = column.getName();
            ColumnType columnType = column.getColumnType();
            BiConsumer setter = column.getSetter();
            switch (columnType){
                case Long:
                    setter.accept(item, resultSet.getLong(name));
                    break;
                case String:
                    setter.accept(item, resultSet.getString(name));
                    break;
                case Reference:
                    Function finder = finders.get(name);
                    long id = resultSet.getLong(name);
                    Object referent = finder.apply(id);
                    setter.accept(item, referent);
                    break;
            }
        }
        return item;
    }

    public static <T> List<T> read(Connection connection, String table, List<Column> columnList, List<Long> ids,
                                   Supplier<T> supplier, Map<String,Function> finders){

        Statement statement = null;
        ResultSet resultSet = null;

        try {

            statement = connection.createStatement();

            StringBuilder sql =  new StringBuilder("select ");
            sql.append(DaoHelper.columnsAsString(columnList));
            sql.append(" from ");
            sql.append(table);
            if( ids.size() == 1 ){
                sql.append(" where id = ");
                sql.append(ids.get(0));
            } else if ( ids.size() > 1 ){
                sql.append(" where id in (");
                sql.append(String.join(", ", ids.stream().map(l -> l.toString()).collect(Collectors.toList())));
                sql.append(" )");
            }

            resultSet = statement.executeQuery(sql.toString());

            List<T> items = new ArrayList<>();

            while (resultSet.next()) {
                T item = DaoHelper.populate(resultSet, columnList, supplier, finders);
                items.add(item);
            }

            return items;
        } catch (SQLException se){
            throw new RuntimeException(se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException se){
                //TODO : Log here.
            }
        }
    }

    public static <T extends Identifiable> long save(Connection connection, String table, List<Column> columnList, T item, Map<String, Long> references) {
        if( item.getId() == null ){
            return doInsert(connection, table, columnList, item, references);
        } else {
            return doUpdate(connection, table, columnList, item, references);
        }
    }

    static <T> T fromSingletonList(List<T> items, String errorMsg) {
        if (items.isEmpty()) {
            return null;
        }
        if (items.size() == 1) {
            return items.get(0);
        }
        throw new RuntimeException("Found " + items.size() + " items. Message: " + errorMsg);
    }


}
