package org.center4racialjustice.legup.db.hrorm;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

public class DaoImpl<T> implements Dao<T> {

    private final Connection connection;
    private final Table table;
    private final List<TypedColumn<T>> columns;

    public DaoImpl(Connection connection, Table table, List<TypedColumn<T>> columns){
        this.connection = connection;
        this.table = table;
        this.columns = columns;
    }

    public String insertStatement(){
        return DaoHelper.insertStatement(table.getName(), columns, Collections.emptyList());
    }

    @Override
    public void insert(T item) {
        String sql = insertStatement();
        DaoHelper.runInsertOrUpdate(connection, sql, columns, Collections.emptyList(), item);
    }

    @Override
    public void update(T item) {

    }

    @Override
    public void delete(T item) {

    }

    @Override
    public T select(long id) {
        return null;
    }

    @Override
    public List<T> selectAll() {
        return null;
    }
}
