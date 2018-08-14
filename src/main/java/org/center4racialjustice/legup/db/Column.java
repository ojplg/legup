package org.center4racialjustice.legup.db;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Column<O, T> {

    private final String name;
    private final ColumnType columnType;
    private final Function<O,T> getter;
    private final BiConsumer<O,T> setter;

    public Column(String name, ColumnType columnType, Function<O, T> getter, BiConsumer<O,T> setter){
        this.name = name;
        this.columnType = columnType;
        this.getter = getter;
        this.setter = setter;
    }

    public String getName() {
        return name;
    }

    public Function<O, T> getGetter() {
        return getter;
    }

    public BiConsumer<O, T> getSetter() {
        return setter;
    }

    public ColumnType getColumnType(){
        return columnType;
    }
}
