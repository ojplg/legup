package org.center4racialjustice.legup.db.hrorm;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class DaoImpl<T> implements Dao<T> {

    private final Connection connection;
    private final Table table;
    private final List<TypedColumn<T>> columns;
    private final PrimaryKey<T> primaryKey;
    private final Supplier<T> supplier;

    public DaoImpl(Connection connection, Table table, List<TypedColumn<T>> columns, PrimaryKey<T> primaryKey, Supplier<T> supplier){
        this.connection = connection;
        this.table = table;
        this.columns = Collections.unmodifiableList(columns);
        this.primaryKey = primaryKey;
        this.supplier = supplier;
    }

    public String tableName(){
        return table.getName();
    }

    public List<TypedColumn<T>> getColumns(){
        return columns;
    }

    public String insertSql(){
        return DaoHelper.insertStatement(table.getName(), columns, Collections.emptyList());
    }

    public String updateSql(T item){
        return DaoHelper.updateStatement(table.getName(), columns, Collections.emptyList(), item, primaryKey);
    }

    public String deleteSql(T item){
        // should not have to use "id"
        return "delete from " + table.getName()+ " where id = " + primaryKey.getKey(item);
    }

    @Override
    public void insert(T item) {
        String sql = insertSql();
        DaoHelper.runInsertOrUpdate(connection, sql, columns, Collections.emptyList(), item);
    }

    @Override
    public void update(T item) {
        String sql = updateSql(item);
        DaoHelper.runInsertOrUpdate(connection, sql, columns, Collections.emptyList(), item);
    }

    @Override
    public void delete(T item) {
        String sql = deleteSql(item);
        DaoHelper.runDelete(connection, sql);
    }

    @Override
    public T select(long id) {
        String sql = DaoHelper.selectString(table.getName(), columns);
        List<T> items = DaoHelper.read(connection, sql, columns, supplier);
        return DaoHelper.fromSingletonList(items, "");
    }

    @Override
    public List<T> selectAll() {
        return null;
    }
}
