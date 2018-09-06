package org.center4racialjustice.legup.db.hrorm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public DaoImpl(Connection connection, String tableName, Supplier<T> supplier, PrimaryKey<T> primaryKey,
                   List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns,
                   List<ChildrenDescriptor<T,?>> childrenDescriptors){
        this.connection = connection;
        this.tableName = tableName;
        this.dataColumns = Collections.unmodifiableList(dataColumns);
        this.primaryKey = primaryKey;
        this.supplier = supplier;
        this.joinColumns = Collections.unmodifiableList(joinColumns);
        this.childrenDescriptors = childrenDescriptors;
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

    public String insertSql(){
        return DaoHelper.insertStatement(tableName, dataColumns, joinColumns);
    }

    public String updateSql(T item){
        return DaoHelper.updateStatement(tableName, dataColumns, joinColumns, item, primaryKey);
    }

    public String deleteSql(T item){
        return "delete from " + tableName + " where " + primaryKey.keyName() + " = " + primaryKey.getKey(item);
    }

    @Override
    public long insert(T item) {
        String sql = insertSql();
        long id = DaoHelper.runInsertOrUpdate(connection, sql, allColumns(), item);
        primaryKey.setKey(item, id);
        for(ChildrenDescriptor<T,?> childrenDescriptor : childrenDescriptors){
            childrenDescriptor.saveChildren(connection, item);
        }
        return id;
    }

    @Override
    public void update(T item) {
        String sql = updateSql(item);
        DaoHelper.runInsertOrUpdate(connection, sql, allColumns(), item);
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
        String sql = DaoHelper.joinSelectSql(tableName, dataColumns, joinColumns);
        sql = sql + " and a." + primaryKey.keyName() + " = " + id;
        List<T> items = DaoHelper.read(connection, sql, allColumns(), supplier, childrenDescriptors);
        return DaoHelper.fromSingletonList(items, "");
    }

    @Override
    public List<T> selectMany(List<Long> ids) {
        String sql = DaoHelper.joinSelectSql(tableName, dataColumns, joinColumns);
        List<String> idStrings = ids.stream().map(Object::toString).collect(Collectors.toList());
        String idsString = String.join(",", idStrings);
        sql = sql + " and a." + primaryKey.keyName() + " in (" + idsString + ")";
        return DaoHelper.read(connection, sql, allColumns(), supplier, childrenDescriptors);
    }

    @Override
    public List<T> selectAll() {
        String sql = DaoHelper.joinSelectSql(tableName, dataColumns, joinColumns);
        return DaoHelper.read(connection, sql, allColumns(), supplier, childrenDescriptors);
    }

    @Override
    public T selectByColumns(T item, List<String> columnNames){
        List<T> items = selectManyByColumns(item, columnNames);
        return DaoHelper.fromSingletonList(items, "");
    }

    @Override
    public List<T> selectManyByColumns(T item, List<String> columnNames) {
        StringBuilder buf = new StringBuilder();
        buf.append(DaoHelper.joinSelectSql(tableName, dataColumns, joinColumns));
        buf.append(" and ");
        for(int idx=0 ; idx < columnNames.size() ; idx++ ){
            String columnName = columnNames.get(idx);
            buf.append("a");
            buf.append(".");
            buf.append(columnName);
            buf.append(" = ?");
            if( idx < columnNames.size() - 1 ) {
                buf.append(" and ");
            }
        }
        String sql = buf.toString();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            for(int idx=0 ; idx < columnNames.size() ; idx++ ){
                String columnName = columnNames.get(idx);
                TypedColumn<T> column = columnMap(columnNames).get(columnName);
                column.setValue(item, idx + 1, statement);
            }
            ResultSet resultSet = statement.executeQuery();
            List<T> items = new ArrayList<>();

            while (resultSet.next()) {
                T t = DaoHelper.populate( resultSet, supplier, allColumns());
                items.add(t);
            }

            return items;
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
}
