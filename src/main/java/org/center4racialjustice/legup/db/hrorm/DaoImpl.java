package org.center4racialjustice.legup.db.hrorm;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DaoImpl<T> implements Dao<T>, DaoDescriptor<T> {

    private final Connection connection;
    private final String tableName;
    private final List<TypedColumn<T>> dataColumns;
    private final PrimaryKey<T> primaryKey;
    private final Supplier<T> supplier;
    private final List<JoinColumn<T,?>> joinColumns;
    private final List<ChildrenDescriptor<T,?>> childrenDescriptors;
    private final SqlBuilder<T> sqlBuilder;
    private final SqlRunner<T> sqlRunner;

    public DaoImpl(Connection connection,
                   String tableName,
                   Supplier<T> supplier,
                   PrimaryKey<T> primaryKey,
                   List<TypedColumn<T>> dataColumns,
                   List<JoinColumn<T,?>> joinColumns,
                   List<ChildrenDescriptor<T,?>> childrenDescriptors){
        this.connection = connection;
        this.tableName = tableName;
        this.dataColumns = Collections.unmodifiableList(dataColumns);
        this.primaryKey = primaryKey;
        this.supplier = supplier;
        this.joinColumns = Collections.unmodifiableList(joinColumns);
        this.childrenDescriptors = childrenDescriptors;
        this.sqlBuilder = new SqlBuilder<>(tableName, this.dataColumns, this.joinColumns, primaryKey);
        this.sqlRunner = new SqlRunner<>(connection, dataColumns, joinColumns);
    }

    public String tableName(){
        return tableName;
    }

    public List<TypedColumn<T>> dataColumns(){
        return dataColumns;
    }

    public List<JoinColumn<T, ?>> joinColumns(){
        return joinColumns;
    }

    public Supplier<T> supplier() { return supplier; }

    public PrimaryKey<T> primaryKey() { return primaryKey; }

    public String deleteSql(T item){
        return "delete from " + tableName + " where " + primaryKey.keyName() + " = " + primaryKey.getKey(item);
    }

    @Override
    public long insert(T item) {
        String sql = sqlBuilder.insert();
        long id = DaoHelper.getNextSequenceValue(connection, primaryKey.getSequenceName());
        primaryKey.setKey(item, id);
        sqlRunner.insert(sql, item);
        for(ChildrenDescriptor<T,?> childrenDescriptor : childrenDescriptors){
            childrenDescriptor.saveChildren(connection, item);
        }
        return id;
    }

    @Override
    public void update(T item) {
        String sql = sqlBuilder.update(item);
        sqlRunner.update(sql, item);
        for(ChildrenDescriptor<T,?> childrenDescriptor : childrenDescriptors){
            childrenDescriptor.saveChildren(connection, item);
        }
    }

    @Override
    public void delete(T item) {
        String sql = deleteSql(item);
        DaoHelper.runDelete(connection, sql);
    }

    @Override
    public T select(long id) {
        String sql = sqlBuilder.select();
        sql = sql + " and a." + primaryKey.keyName() + " = " + id;
        List<T> items = sqlRunner.select(sql, supplier, childrenDescriptors);
        return fromSingletonList(items);
    }

    @Override
    public List<T> selectMany(List<Long> ids) {
        String sql = sqlBuilder.select();
        List<String> idStrings = ids.stream().map(Object::toString).collect(Collectors.toList());
        String idsString = String.join(",", idStrings);
        sql = sql + " and a." + primaryKey.keyName() + " in (" + idsString + ")";
        return sqlRunner.select(sql, supplier, childrenDescriptors);
    }

    @Override
    public List<T> selectAll() {
        String sql = sqlBuilder.select();
        return sqlRunner.select(sql, supplier, childrenDescriptors);
    }

    @Override
    public T selectByColumns(T item, List<String> columnNames){
        List<T> items = selectManyByColumns(item, columnNames);
        return fromSingletonList(items);
    }

    @Override
    public List<T> selectManyByColumns(T item, List<String> columnNames) {
        String sql = sqlBuilder.selectByColumns(columnNames);
        return sqlRunner.selectByColumns(sql, supplier, columnNames, columnMap(columnNames), item);
    }

    private <A> A fromSingletonList(List<A> items) {
        if (items.isEmpty()) {
            return null;
        }
        if (items.size() == 1) {
            return items.get(0);
        }
        throw new RuntimeException("Found " + items.size() + " items.");
    }
}
