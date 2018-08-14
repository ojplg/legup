package org.center4racialjustice.legup.db;

import java.util.function.BiConsumer;
import java.util.function.Function;

class Column<O, T> {

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

    String getName() {
        return name;
    }

    Function<O, T> getGetter() {
        return getter;
    }

    BiConsumer<O, T> getSetter() {
        return setter;
    }

    ColumnType getColumnType(){
        return columnType;
    }
}
