package org.center4racialjustice.legup.db.hrorm;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

public class DaoImpl<T> implements Dao<T> {

    private final Connection connection;
    private final Table table;
    private final List<TypedColumn<T>> columns;
    private final PrimaryKey<T> primaryKey;

    public DaoImpl(Connection connection, Table table, List<TypedColumn<T>> columns, PrimaryKey<T> primaryKey){
        this.connection = connection;
        this.table = table;
        this.columns = columns;
        this.primaryKey = primaryKey;
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
        String sql = DaoHelper.updateStatement(table.getName(), columns, Collections.emptyList(), item, primaryKey);
        DaoHelper.runInsertOrUpdate(connection, sql, columns, Collections.emptyList(), item);
    }

    @Override
    public void delete(T item) {
        // should not have to use "id"
        String sql = "delete from " + table.getName() + " where id = " + primaryKey.getKey(item);
        DaoHelper.runDelete(connection, sql);
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
