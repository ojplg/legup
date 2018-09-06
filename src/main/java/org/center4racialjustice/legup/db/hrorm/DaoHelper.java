package org.center4racialjustice.legup.db.hrorm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        sql.append(" RETURNING ID");
        return sql.toString();
    }

    public static <T> String joinSelectSql(String table, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns){
        StringBuilder buf = new StringBuilder();
        buf.append("select ");
        buf.append(DaoHelper.typedColumnsAsString("a", dataColumns, true));
        for(JoinColumn joinColumn : joinColumns) {
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
        bldr.append(" ) values ( DEFAULT, ");
        int end = columnList.size() - 2;
        if (!joinColumns.isEmpty()){
            end += joinColumns.size();
        }
        for(int idx=0; idx<end; idx++){
            bldr.append("?, ");
        }
        bldr.append("? ");
        bldr.append(" ) ");
        bldr.append(" RETURNING ID");


        return bldr.toString();
    }

    public static <T> Long runInsertOrUpdate(Connection connection, String sql, List<TypedColumn<T>> columnList, List<JoinColumn<T,?>> joinColumns, T item) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            int index = 1;
            for ( ; index < columnList.size(); index++) {
                TypedColumn column = columnList.get(index);
                column.setValue(item, index, preparedStatement);
            }

            for( JoinColumn joinColumn : joinColumns ){
                joinColumn.setValue(item, index, preparedStatement);
                index++;
            }

            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong("ID");

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

    public static <T> Long doInsert(Connection connection, String table, List<TypedColumn<T>> columnList, List<JoinColumn<T,?>> joinColumns, T item){
        String sql = DaoHelper.insertStatement(table, columnList, joinColumns);
        return runInsertOrUpdate(connection, sql, columnList, joinColumns, item);
    }

    public static void runDelete(Connection connection, String sql) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql) ){
            preparedStatement.execute();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    public static <T> long doUpdate(Connection connection, String table, List<TypedColumn<T>> columnList, List<JoinColumn<T,?>> joinColumns, PrimaryKey<T> primaryKey, T item){
        String sql = DaoHelper.updateStatement(table, columnList, joinColumns, item , primaryKey);
        return runInsertOrUpdate(connection, sql, columnList, joinColumns, item);
    }

    public static <T> T populate(ResultSet resultSet, List<TypedColumn<T>> columnList, Supplier<T> supplier) throws SQLException {
        return populate("", resultSet, supplier, columnList, Collections.emptyList());
    }

    public static <T> T populate(String prefix, ResultSet resultSet, Supplier<T> supplier, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns)
            throws SQLException {
        T item = supplier.get();

        for (TypedColumn<T> column: dataColumns) {
            column.populate(item, resultSet);
        }

        for (JoinColumn<T,?> column : joinColumns){
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
                T item = DaoHelper.populate(resultSet, allColumns, supplier);
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

    public static <T> Map<String, TypedColumn<T>> buildColumnMap(List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns){
        Map<String, TypedColumn<T>> map = new HashMap<>();
        for(TypedColumn<T> column : dataColumns){
            map.put(column.getName(), column);
        }
        for(TypedColumn<T> column : joinColumns){
            map.put(column.getName(), column);
        }
        return Collections.unmodifiableMap(map);
    }


    public static <T> String selectByColumns(String tableName, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns, List<String> columnNames){
        Map<String, TypedColumn<T>> columnMap = buildColumnMap(dataColumns, joinColumns);

        StringBuilder buf = new StringBuilder();
        buf.append(DaoHelper.joinSelectSql(tableName, dataColumns, joinColumns));
        buf.append(" and ");
        for(int idx=0 ; idx < columnNames.size() ; idx++ ){
            String columnName = columnNames.get(idx);
            TypedColumn<T> column = columnMap.get(columnName);
            buf.append("a" + "." + column.getName());
            buf.append(" = ?");
            if( idx < columnNames.size() - 1 ) {
                buf.append(" and ");
            }
        }
        String sql = buf.toString();

        return sql;
    }

    public static <T> List<T> runSelectByColumns(Connection connection, String sql, Supplier<T> supplier, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns,
                                                 List<String> columnNames, T item){
        Map<String, TypedColumn<T>> columnMap = buildColumnMap(dataColumns, joinColumns);
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            for(int idx=0 ; idx < columnNames.size() ; idx++ ){
                String columnName = columnNames.get(idx);
                TypedColumn<T> column = columnMap.get(columnName);
                column.setValue(item, idx + 1, statement);
            }
            ResultSet resultSet = statement.executeQuery();
            List<T> items = new ArrayList<>();

            while (resultSet.next()) {
                T t = DaoHelper.populate("", resultSet, supplier, dataColumns, joinColumns);
                items.add(t);
            }

            return items;
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }

    }

    public static <T> List<T> doSelect(Connection connection, String sql, Supplier<T> supplier, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns){

        Statement statement = null;
        ResultSet resultSet = null;

        List<T> items = new ArrayList<>();

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                T item = supplier.get();
                for(TypedColumn column : dataColumns){
                    column.populate(item, resultSet);
                }

                for(JoinColumn joinColumn : joinColumns ){
                    joinColumn.populate(item, resultSet);
                }

                items.add(item);
            }
            return items;

        } catch (SQLException se) {
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
