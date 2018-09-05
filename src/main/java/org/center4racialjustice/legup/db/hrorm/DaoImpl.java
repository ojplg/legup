package org.center4racialjustice.legup.db.hrorm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DaoImpl<T> implements Dao<T>, DaoDescriptor<T> {

    private final Connection connection;
    private final String tableName;
    private final List<TypedColumn<T>> columns;
    private final PrimaryKey<T> primaryKey;
    private final Supplier<T> supplier;
    private final Map<String, TypedColumn<T>> columnMap = new HashMap<>();
    private final List<JoinColumn<T,?>> joinColumns;

    public DaoImpl(Connection connection, String tableName, Supplier<T> supplier, PrimaryKey<T> primaryKey, List<TypedColumn<T>> columns, List<JoinColumn<T,?>> joinColumns){
        this.connection = connection;
        this.tableName = tableName;
        this.columns = Collections.unmodifiableList(columns);
        this.primaryKey = primaryKey;
        this.supplier = supplier;
        this.joinColumns = Collections.unmodifiableList(joinColumns);

        for(TypedColumn<T> column : columns){
            columnMap.put(column.getName(), column);
        }
    }

    public String tableName(){
        return tableName;
    }

    public List<TypedColumn<T>> dataColumns(){
        return columns;
    }

    public Supplier<T> supplier() { return supplier; }

    public PrimaryKey<T> primaryKey() { return primaryKey; }

    public String insertSql(){
        return DaoHelper.insertStatement(tableName, columns, joinColumns);
    }

    public String updateSql(T item){
        return DaoHelper.updateStatement(tableName, columns, joinColumns, item, primaryKey);
    }

    public String deleteSql(T item){
        return "delete from " + tableName + " where " + primaryKey.keyName() + " = " + primaryKey.getKey(item);
    }

    @Override
    public long insert(T item) {
        String sql = insertSql();
        long id = DaoHelper.runInsertOrUpdate(connection, sql, columns, joinColumns, item);
        primaryKey.setKey(item, id);
        return id;
    }

    @Override
    public void update(T item) {
        String sql = updateSql(item);
        DaoHelper.runInsertOrUpdate(connection, sql, columns, joinColumns, item);
    }

    @Override
    public void delete(T item) {
        String sql = deleteSql(item);
        DaoHelper.runDelete(connection, sql);
    }

    @Override
    public T select(long id) {
        String sql = DaoHelper.baseSelectSql(tableName, columns);
        sql = sql + " where " + primaryKey.keyName() + " = " + id;
        List<T> items = DaoHelper.read(connection, sql, columns, supplier);
        return DaoHelper.fromSingletonList(items, "");
    }

    @Override
    public List<T> selectMany(List<Long> ids) {
        String sql = DaoHelper.baseSelectSql(tableName, columns);
        List<String> idStrings = ids.stream().map(l -> l.toString()).collect(Collectors.toList());
        String idsString = String.join(",", idStrings);
        sql = sql + " where " + primaryKey.keyName() + " in (" + idsString + ")";
        return DaoHelper.read(connection, sql, columns, supplier);
    }


    @Override
    public List<T> selectAll() {
        String sql = DaoHelper.baseSelectSql(tableName, columns);
        List<T> items = DaoHelper.read(connection, sql, columns, supplier);
        return items;
    }

    @Override
    public T selectByColumns(T item, List<String> columnNames){
        List<T> items = selectManyByColumns(item, columnNames);
        return DaoHelper.fromSingletonList(items, "");
    }

    @Override
    public List<T> selectManyByColumns(T item, List<String> columnNames) {
        StringBuilder buf = new StringBuilder();
        buf.append(DaoHelper.baseSelectSql(tableName, columns));
        buf.append(" where ");
        for(int idx=0 ; idx < columnNames.size() ; idx++ ){
            String columnName = columnNames.get(idx);
            TypedColumn<T> column = columnMap.get(columnName);
            buf.append(columnName);
            buf.append(" = ?");
            if( idx < columnNames.size()) {
                buf.append(" and ");
            }
        }
        String sql = buf.toString();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            for(int idx=0 ; idx < columnNames.size() ; idx++ ){
                String columnName = columnNames.get(idx);
                TypedColumn<T> column = columnMap.get(columnName);
                column.setValue(item, idx, statement);
            }
            ResultSet resultSet = statement.executeQuery();
            List<T> items = new ArrayList<>();

            while (resultSet.next()) {
                T t = DaoHelper.populate(resultSet, columns, supplier);
                items.add(t);
            }

            return items;
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
}
