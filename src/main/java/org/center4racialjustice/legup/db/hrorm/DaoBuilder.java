package org.center4racialjustice.legup.db.hrorm;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DaoBuilder<T> {

    private final Table table;
    private final List<TypedColumn<T>> columns = new ArrayList<>();
    private PrimaryKey<T> primaryKey;

    public DaoBuilder(Table table){
        this.table = table;
    }

    public Dao<T> buildDao(Connection connection){
        return new DaoImpl<>(connection, table, columns, primaryKey);
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

    public DaoBuilder withPrimaryKey(String columnName, Function<T, Long> getter, BiConsumer<T, Long> setter){

        this.primaryKey = new PrimaryKey<T>() {
            @Override
            public Long getKey(T item) {
                return getter.apply(item);
            }

            @Override
            public void setKey(T item, Long id) {
                setter.accept(item, id);
            }
        };
        TypedColumn<T> column = new LongColumn<>(columnName, "", getter, setter);
        columns.add(column);
        return this;
    }

}
