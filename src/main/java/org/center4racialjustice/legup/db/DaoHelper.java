package org.center4racialjustice.legup.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Identifiable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class DaoHelper {

    private static final Logger log = LogManager.getLogger(DaoHelper.class);

    public static String typedColumnsAsString(String prefix, List<? extends TypedColumn> columnList, boolean withAliases){
        Function<TypedColumn,String> stringer;
        if( withAliases && prefix != null ) {
            stringer = c -> prefix + "." + c.getName() + " as " + prefix + c.getName();
        } else if (prefix != null ){
            stringer = c -> prefix + c.getName();
        } else {
            stringer = c -> c.getName();
        }
        List<String> columnNames = columnList.stream().map(stringer).collect(Collectors.toList());
        return String.join(", ", columnNames);
    }

    public static <T> String insertStatement(String table, List<TypedColumn<T>> columnList, List<JoinColumn<T,?>> joinColumns){
        StringBuilder bldr = new StringBuilder();
        bldr.append("insert into ");
        bldr.append(table);
        bldr.append(" ( ");
        bldr.append(DaoHelper.typedColumnsAsString("",columnList, false));
        if( ! joinColumns.isEmpty() ) {
            bldr.append(", ");
            bldr.append(DaoHelper.typedColumnsAsString("", joinColumns, false));
        }
        bldr.append(" ) values ( DEFAULT, ");
        for(int idx=0; idx<columnList.size()+joinColumns.size()-2; idx++){
            bldr.append("?, ");
        }
        bldr.append("? ");
        bldr.append(" ) ");
        bldr.append(" RETURNING ID ");

        return bldr.toString();
    }

    private static <T> String insertStatement(String table, List<TypedColumn<T>> columnList) {
        return insertStatement(table, columnList, Collections.emptyList());
    }

    private static <T extends Identifiable> String updateStatement(String table, List<TypedColumn<T>> columnList, T item){
        StringBuilder sql = new StringBuilder("update ");
        sql.append(table);
        sql.append(" set ");
        for(int idx=1; idx<columnList.size(); idx++){
            TypedColumn column = columnList.get(idx);
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

    private static <T extends Identifiable> Long doInsert(Connection connection, String table, List<TypedColumn<T>> columnList, T item){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = DaoHelper.insertStatement(table, columnList);

        try {
            preparedStatement = connection.prepareStatement(sql);

            for (int index = 1; index < columnList.size(); index++) {
                TypedColumn column = columnList.get(index);
                column.setValue(item, index, preparedStatement);
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
                log.error("Error during close",se);
            }
        }
    }

    public static <T extends Identifiable> long doUpdate(Connection connection, String table, List<TypedColumn<T>> columnList, T item){
        String sql = DaoHelper.updateStatement(table, columnList, item);

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            for(int idx=1; idx<columnList.size(); idx++){
                TypedColumn<T> column = columnList.get(idx);
                column.setValue(item, idx, preparedStatement);
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
                log.error("Error during close",se);
            }
        }
    }

    public static <T> T populate(ResultSet resultSet, List<TypedColumn<T>> columnList, Supplier<T> supplier) throws SQLException {
        return populate("", resultSet, columnList, supplier);
    }

    public static <T> T populate(String prefix,ResultSet resultSet, List<TypedColumn<T>> columnList, Supplier<T> supplier) throws SQLException {
        T item = supplier.get();

        for (TypedColumn<T> column: columnList) {
            column.populate(item, resultSet);
        }
        return item;
    }


    public static <T> String selectString(String table, List<TypedColumn<T>> columnList){
        StringBuilder sql =  new StringBuilder("select ");
        sql.append(DaoHelper.typedColumnsAsString("",columnList, false));
        sql.append(" from ");
        sql.append(table);
        return sql.toString();
    }

    public static <T> List<T> read(Connection connection, String sql, List<TypedColumn<T>> dataColumns, Supplier<T> supplier){
        Statement statement = null;
        ResultSet resultSet = null;

        try {

            statement = connection.createStatement();

            resultSet = statement.executeQuery(sql);

            List<T> items = new ArrayList<>();

            while (resultSet.next()) {
                T item = DaoHelper.populate(resultSet, dataColumns, supplier);
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
                log.error("Error during close",se);
            }
        }

    }

    public static <T> List<T> read(Connection connection, String table, List<TypedColumn<T>> dataColumns, List<Long> ids,
                                   Supplier<T> supplier){

        StringBuilder sql =  new StringBuilder(selectString(table, dataColumns));
        if( ids.size() == 1 ){
            sql.append(" where id = ");
            sql.append(ids.get(0));
        } else if ( ids.size() > 1 ){
            sql.append(" where id in (");
            sql.append(String.join(", ", ids.stream().map(l -> l.toString()).collect(Collectors.toList())));
            sql.append(" )");
        }

        return read(connection, sql.toString(), dataColumns, supplier);
    }

    public static <T extends Identifiable> long save(Connection connection, String table, List<TypedColumn<T>> columnList, T item) {
        if( item.getId() == null ){
            return doInsert(connection, table, columnList, item);
        } else {
            return doUpdate(connection, table, columnList, item);
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
