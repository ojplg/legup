package org.center4racialjustice.legup.db;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ReferenceColumn<O,T> implements ColumnDescription {

    private final String name;
    private final String prefix;
    private final String table;
    private final List<Column> columnList;
    private final Supplier<T> supplier;
    private final BiConsumer<O, T> setter;

    public ReferenceColumn(String name, String prefix, String table, List<Column> columnList, Supplier<T> supplier, BiConsumer<O, T> setter) {
        this.name = name;
        this.prefix = prefix;
        this.table = table;
        this.columnList = columnList;
        this.supplier = supplier;
        this.setter = setter;
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

    public BiConsumer<O, T> getSetter(){
        return setter;
    }
}
