package org.center4racialjustice.legup.db.hrorm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableBuilder {

    private String tableName;
    private List<Column> columns = new ArrayList();

    public Table build(){
        return new Table(tableName, Collections.unmodifiableList(columns));
    }

    public TableBuilder withTableName(String tableName){
        this.tableName = tableName;
        return this;
    }

    public TableBuilder withColumn(String columnName, ColumnType columnType){
        Column column = new Column(columnName, columnType);
        columns.add(column);
        return this;
    }
}
