package org.center4racialjustice.legup.db;

import java.util.function.BiConsumer;
import java.util.function.Function;

class Column<O, T> implements ColumnDescription {

    private final String name;
    private final ColumnType columnType;
    private final Function<O,T> getter;
    private final BiConsumer<O,T> setter;

    Column(String name, ColumnType columnType, Function<O, T> getter, BiConsumer<O,T> setter){
        this.name = name;
        this.columnType = columnType;
        this.getter = getter;
        this.setter = setter;
    }

    public String getName() {
        return name;
    }

    Function<O, T> getGetter() {
        return getter;
    }

    BiConsumer<O, T> getSetter() {
        return setter;
    }

    public ColumnType getColumnType(){
        return columnType;
    }

    public boolean isReference(){
        return ColumnType.Reference.equals(getColumnType());
    }

    public String getPrefix(){
        throw new RuntimeException("No prefix for regular column");
    }
}
