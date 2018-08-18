package org.center4racialjustice.legup.db;

import java.util.List;
import java.util.function.Supplier;

public class ReferenceColumn<T> implements ColumnDescription {

    private final String name;
    private final String prefix;
    private final String table;
    private final List<Column> columnList;
    private final Supplier<T> supplier;

    public ReferenceColumn(String name, String prefix, String table, List<Column> columnList, Supplier<T> supplier) {
        this.name = name;
        this.prefix = prefix;
        this.table = table;
        this.columnList = columnList;
        this.supplier = supplier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ColumnType getColumnType() {
        return ColumnType.Reference;
    }

    public boolean isReference(){
        return true;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTable() {
        return table;
    }

    public List<Column> getColumnList() {
        return columnList;
    }

    public Supplier<T> getSupplier(){
        return supplier;
    }
}
