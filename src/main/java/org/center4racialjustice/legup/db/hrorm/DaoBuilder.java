package org.center4racialjustice.legup.db.hrorm;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DaoBuilder<T> {

    private final Table table;
    private final List<TypedColumn<T>> columns = new ArrayList<>();

    public DaoBuilder(Table table){
        this.table = table;
    }

    public Dao<T> buildDao(Connection connection){
        return new DaoImpl<>(connection, table, columns);
    }

    public DaoBuilder withStringMapping(String columnName, Function<T, String> getter, BiConsumer<T, String> setter){
        TypedColumn<T> column = new StringColumn<>(columnName, "", getter, setter);
        columns.add(column);
        return this;
    }

    public DaoBuilder withIntegerMapping(String columnName, Function<T, Long> getter, BiConsumer<T, Long> setter){
        TypedColumn<T> column = new LongColumn<>(columnName, "", getter, setter);
        columns.add(column);
        return this;
    }

}
