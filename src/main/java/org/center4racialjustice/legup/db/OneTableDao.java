package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Identifiable;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class OneTableDao<T extends Identifiable> implements Dao<T> {

    private final Supplier<T> supplier;
    private final String table;
    private final List<Column> columnList;

    private final Connection connection;

    OneTableDao(Connection connection, Supplier<T> supplier, String table, List<Column> columnList) {
        this.supplier = supplier;
        this.table = table;
        this.columnList = columnList;
        this.connection = connection;
    }

    public long save(T item){
        return DaoHelper.save(connection, table, columnList, item);
    }

    public T read(long id){
        List<T> found = DaoHelper.read(connection, table, columnList, Collections.singletonList(id), supplier);
        return DaoHelper.fromSingletonList(found, "Table: " + table + ", ID: " + id);
    }

    public List<T> readAll(){
        return DaoHelper.read(connection, table, columnList, Collections.emptyList(), supplier);
    }
}