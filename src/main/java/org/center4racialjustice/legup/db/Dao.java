package org.center4racialjustice.legup.db;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class Dao<T extends Identifiable> {

    private final Supplier<T> supplier;
    private final String table;
    private final List<Column> columnList;

    private final Connection connection;

    public Dao(Connection connection, Supplier<T> supplier, String table, List<Column> columnList) {
        this.supplier = supplier;
        this.table = table;
        this.columnList = columnList;
        this.connection = connection;
    }

    public long save(T item){
        return DaoHelper.save(item, table, columnList, connection);
    }

    public T read(long id){
        List<T> found = DaoHelper.read(connection, table, columnList, Collections.singletonList(id), supplier);

        if (found.isEmpty()){
            return null;
        }
        if( found.size() == 1){
            return found.get(0);
        }
        throw new RuntimeException("Found " + found.size() + " items with id " + id + " in table " + table);
    }

    public List<T> readAll(){
        return DaoHelper.read(connection, table, columnList, Collections.emptyList(), supplier);
    }
}
