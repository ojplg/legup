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

public class DaoImpl<T> implements Dao<T> {

    private final Connection connection;
    private final Table table;
    private final List<TypedColumn<T>> columns;
    private final PrimaryKey<T> primaryKey;
    private final Supplier<T> supplier;
    private final Map<String, TypedColumn<T>> columnMap = new HashMap<>();


    public DaoImpl(Connection connection, Table table, List<TypedColumn<T>> columns, PrimaryKey<T> primaryKey, Supplier<T> supplier){
        this.connection = connection;
        this.table = table;
        this.columns = Collections.unmodifiableList(columns);
        this.primaryKey = primaryKey;
        this.supplier = supplier;

        for(TypedColumn<T> column : columns){
            columnMap.put(column.getName(), column);
        }
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
        return "delete from " + table.getName()+ " where " + primaryKey.keyName() + " = " + primaryKey.getKey(item);
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
        String sql = DaoHelper.baseSelectSql(table.getName(), columns);
        sql = sql + " where " + primaryKey.keyName() + " = " + id;
        List<T> items = DaoHelper.read(connection, sql, columns, supplier);
        return DaoHelper.fromSingletonList(items, "");
    }

    @Override
    public List<T> selectAll() {
        String sql = DaoHelper.baseSelectSql(table.getName(), columns);
        List<T> items = DaoHelper.read(connection, sql, columns, supplier);
        return items;
    }

    @Override
    public T selectByColumns(T item, List<String> columnNames){
        StringBuilder buf = new StringBuilder();
        buf.append(DaoHelper.baseSelectSql(table.getName(), columns));
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

            return DaoHelper.fromSingletonList(items, "");

        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
}
