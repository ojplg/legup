package org.center4racialjustice.legup.db.hrorm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DaoHelper {

    private static final Logger log = LogManager.getLogger(DaoHelper.class);

    static String typedColumnsAsString(String prefix, List<? extends TypedColumn> columnList, boolean withAliases){
        Function<TypedColumn,String> stringer;
        if( withAliases && prefix != null ) {
            stringer = c -> prefix + "." + c.getName() + " as " + prefix + c.getName();
        } else if (prefix != null ){
            stringer = c -> prefix + c.getName();
        } else {
            stringer = TypedColumn::getName;
        }
        List<String> columnNames = columnList.stream().map(stringer).collect(Collectors.toList());
        return String.join(", ", columnNames);
    }

    public static <T> String updateStatement(String table, List<TypedColumn<T>> columnList, List<JoinColumn<T,?>> joinColumns, T item, PrimaryKey<T> primaryKey){
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
        for(int idx=0; idx<joinColumns.size(); idx++){
            JoinColumn joinColumn = joinColumns.get(idx);
            sql.append(", ");
            sql.append(joinColumn.getName());
            sql.append(" = ? ");
        }
        sql.append(" where id = ");
        sql.append(primaryKey.getKey(item));
        return sql.toString();
    }

    public static <T> String joinSelectSql(String table, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns){
        StringBuilder buf = new StringBuilder();
        buf.append("select ");
        buf.append(DaoHelper.typedColumnsAsString("a", dataColumns, true));
        for(JoinColumn<T,?> joinColumn : joinColumns) {
            buf.append(", ");
            buf.append(DaoHelper.typedColumnsAsString(
                    joinColumn.getPrefix(),
                    joinColumn.getColumnList(),
                    true
            ));
        }
        buf.append(" from ");
        buf.append(table);
        buf.append(" a");
        for(JoinColumn joinColumn : joinColumns) {
            buf.append(", ");
            buf.append(joinColumn.getTable());
            buf.append(" ");
            buf.append(joinColumn.getPrefix());
        }
        buf.append(" where 1=1 ");
        for( int idx=0; idx<joinColumns.size(); idx++ ){
            JoinColumn joinColumn = joinColumns.get(idx);
            buf.append(" and ");
            buf.append("a.");
            buf.append(joinColumn.getName());
            buf.append("=");
            buf.append(joinColumn.getPrefix());
            buf.append(".id");
        }

        return buf.toString();
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
        bldr.append(" ) values ( ");
        int end = columnList.size() - 1;
        if (!joinColumns.isEmpty()){
            end += joinColumns.size();
        }
        for(int idx=0; idx<end; idx++){
            bldr.append("?, ");
        }
        bldr.append("? ");
        bldr.append(" ) ");

        return bldr.toString();
    }

    public static <T> void runUpdate(Connection connection, String sql, List<TypedColumn<T>> allColumns, T item) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            for(int idx = 1; idx<allColumns.size(); idx++){
                TypedColumn<T> column = allColumns.get(idx);
                column.setValue(item, idx, preparedStatement);
            }

            preparedStatement.execute();

        } catch (SQLException se){
            throw new RuntimeException("Wrapped SQL exception for " + sql, se);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se){
                log.error("Error during close",se);
            }
        }
    }

    public static <T> void runInsert(Connection connection, String sql, List<TypedColumn<T>> allColumns, T item) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            for(int idx = 0; idx<allColumns.size(); idx++){
                TypedColumn<T> column = allColumns.get(idx);
                column.setValue(item, idx + 1, preparedStatement);
            }

            preparedStatement.execute();

        } catch (SQLException se){
            throw new RuntimeException("Wrapped SQL exception for " + sql, se);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se){
                log.error("Error during close",se);
            }
        }
    }

    public static <T> void doInsert(Connection connection, String table, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns, T item){
        String sql = DaoHelper.insertStatement(table, dataColumns, joinColumns);
        runInsert(connection, sql, concatenate(dataColumns, joinColumns), item);
    }

    public static void runDelete(Connection connection, String sql) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql) ){
            preparedStatement.execute();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    public static <T> void doUpdate(Connection connection, String table, List<TypedColumn<T>> columnList, List<JoinColumn<T,?>> dataColumns, PrimaryKey<T> primaryKey, T item){
        String sql = DaoHelper.updateStatement(table, columnList, dataColumns, item , primaryKey);
        runUpdate(connection, sql, concatenate(columnList, dataColumns), item);
    }

    public static <T> T populate(ResultSet resultSet, Supplier<T> supplier, List<TypedColumn<T>> allColumns)
            throws SQLException {
        T item = supplier.get();

        for (TypedColumn<T> column: allColumns) {
            column.populate(item, resultSet);
        }

        return item;
    }

    public static <T> List<T> read(Connection connection, String sql, List<TypedColumn<T>> allColumns, Supplier<T> supplier, List<ChildrenDescriptor<T,?>> childrenDescriptors){
        Statement statement = null;
        ResultSet resultSet = null;

        try {

            statement = connection.createStatement();

            resultSet = statement.executeQuery(sql);

            List<T> items = new ArrayList<>();

            while (resultSet.next()) {
                T item = populate(resultSet, supplier, allColumns);
                for(ChildrenDescriptor<T,?> descriptor : childrenDescriptors){
                    descriptor.populateChildren(connection, item);
                }
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

    public static <T> String selectByColumns(String tableName, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns, Map<String, TypedColumn<T>> columnNameMap){

        StringBuilder buf = new StringBuilder();
        buf.append(DaoHelper.joinSelectSql(tableName, dataColumns, joinColumns));
        for(String columnName : columnNameMap.keySet()){
            buf.append(" and ");
            buf.append(columnName);
            buf.append(" = ? ");
        }

        return buf.toString();
    }

    public static <T> List<T> runSelectByColumns(Connection connection, String sql, Supplier<T> supplier, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns,
                                                 SortedMap<String, TypedColumn<T>> columnNameMap, T item){
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            int idx = 1;
            for(Map.Entry<String, TypedColumn<T>> entry : columnNameMap.entrySet()){
                entry.getValue().setValue(item, idx, statement);
                idx++;
            }
            ResultSet resultSet = statement.executeQuery();
            List<T> items = new ArrayList<>();

            while (resultSet.next()) {
                T t = DaoHelper.populate(resultSet, supplier, concatenate(dataColumns, joinColumns));
                items.add(t);
            }

            return items;
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }

    }

    public static long getNextSequenceValue(Connection connection, String sequenceName) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select nextval('" + sequenceName + "')");
            resultSet.next();
            long value = resultSet.getLong(1);
            return value;
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        } finally {
            try {
                if ( resultSet != null ){
                    resultSet.close();
                }
                if ( statement != null){
                    statement.close();
                }
            } catch (SQLException ex){
                log.error("Could not close", ex);
            }
        }
    }

    public static <T> List<T> concatenate(List<? extends T> as, List<? extends T> bs){
        List<T> list = new ArrayList<>(as);
        list.addAll(bs);
        return list;
    }

    public static <T> T fromSingletonList(List<T> items, String errorMsg) {
        if (items.isEmpty()) {
            return null;
        }
        if (items.size() == 1) {
            return items.get(0);
        }
        throw new RuntimeException("Found " + items.size() + " items. Message: " + errorMsg);
    }

}
