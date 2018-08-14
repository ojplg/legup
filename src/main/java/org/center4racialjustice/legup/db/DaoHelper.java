package org.center4racialjustice.legup.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DaoHelper {

    public static String columnsAsString(List<Column> columnList){
        List<String> columnNames = columnList.stream().map(column -> column.getName()).collect(Collectors.toList());
        return String.join(", ", columnNames);
    }

    public static String insertStatement(String table, List<Column> columnList){
        StringBuilder bldr = new StringBuilder();
        for(int idx=0; idx<columnList.size()-2; idx++){
            bldr.append("?, ");
        }
        bldr.append("? ");

        String sql = "insert into " + table + " ( " + columnsAsString(columnList) + " ) "
                + " values ( DEFAULT, "
                + bldr.toString()
                + " ) "
                + " RETURNING ID ";
        return sql;
    }

    public static <T extends Identifiable> String updateStatement(String table, List<Column> columnList, T item){
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

    public static <T extends Identifiable> Long doInsert(T item, String table, List<Column> columnList, Connection connection){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sql = DaoHelper.insertStatement(table, columnList);

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
                }

            }

            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            long id = resultSet.getLong("id");
            return id;
        } catch (SQLException se){
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

    public static <T extends Identifiable> long doUpdate(T item, String table, List<Column> columnList, Connection connection){
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
                }
            }
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            long id = resultSet.getLong("id");
            return id;
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

}
