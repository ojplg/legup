package org.center4racialjustice.legup.db.hrorm;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DaoBuilder<T> {

    private final Table table;
    private final List<TypedColumn<T>> columns = new ArrayList<>();
    private final List<JoinColumn<T,?>> joinColumns = new ArrayList<>();
    private PrimaryKey<T> primaryKey;
    private Supplier<T> supplier;

    public DaoBuilder(Table table){
        this.table = table;
    }

    public Dao<T> buildDao(Connection connection){

        return new DaoImpl<>(connection, table, columns, primaryKey, supplier);
    }

    public DaoBuilder<T> withSupplier(Supplier<T> supplier){
        this.supplier = supplier;
        return this;
    }

    public DaoBuilder withStringColumn(String columnName, Function<T, String> getter, BiConsumer<T, String> setter){
        TypedColumn<T> column = new StringColumn<>(columnName, "", getter, setter);
        columns.add(column);
        return this;
    }

    public DaoBuilder withIntegerColumn(String columnName, Function<T, Long> getter, BiConsumer<T, Long> setter){
        TypedColumn<T> column = new LongColumn<>(columnName, "", getter, setter);
        columns.add(column);
        return this;
    }

    public <E> DaoBuilder withConvertingStringColumn(String columnName, Function<T, E> getter, BiConsumer<T, E> setter, Converter<String, E> converter){
        TypedColumn<T> column = new StringConverterColumn<>(columnName, "", getter, setter, converter);
        columns.add(column);
        return this;
    }

    public <U> DaoBuilder withJoinColumn(String columnName, String tableName, Supplier<U> supplier, Function<T,U> getter, BiConsumer<T,U> setter, PrimaryKey<U> primaryKey, List<TypedColumn<U>> joinedColumns){
        JoinColumn<T,U> joinColumn = new JoinColumn<T, U>(columnName, "", tableName, getter, setter, supplier, primaryKey, joinedColumns);
        joinColumns.add(joinColumn);
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

            @Override
            public String keyName() { return columnName; }
        };
        TypedColumn<T> column = new LongColumn<>(columnName, "", getter, setter);
        columns.add(column);
        return this;
    }

}
