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
}
