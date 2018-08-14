package org.center4racialjustice.legup.db;

import java.util.List;
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

}
