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
    private final List<TypedColumn<T>> dataColumns;
    private final PrimaryKey<T> primaryKey;
    private final Supplier<T> supplier;
    private final Map<String, TypedColumn<T>> columnMap = new HashMap<>();
    private final List<JoinColumn<T,?>> joinColumns;
    private final List<TypedColumn<T>> allColumns;

    public DaoImpl(Connection connection, String tableName, Supplier<T> supplier, PrimaryKey<T> primaryKey, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns){
        this.connection = connection;
        this.tableName = tableName;
        this.dataColumns = Collections.unmodifiableList(dataColumns);
        this.primaryKey = primaryKey;
        this.supplier = supplier;
        this.joinColumns = Collections.unmodifiableList(joinColumns);

        for(TypedColumn<T> column : dataColumns){
            columnMap.put(column.getName(), column);
        }
        for(TypedColumn<T> column : joinColumns){
            columnMap.put(column.getName(), column);
        }
        List<TypedColumn<T>> tmp = new ArrayList<>();
        tmp.addAll(dataColumns);
        tmp.addAll(joinColumns);
        this.allColumns = Collections.unmodifiableList(tmp);
    }

    public String tableName(){
        return tableName;
    }

    public List<TypedColumn<T>> dataColumns(){
        return dataColumns;
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
        long id = DaoHelper.runInsertOrUpdate(connection, sql, dataColumns, joinColumns, item);
        primaryKey.setKey(item, id);
        return id;
    }

    @Override
    public void update(T item) {
        String sql = updateSql(item);
        DaoHelper.runInsertOrUpdate(connection, sql, dataColumns, joinColumns, item);
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
        List<T> items = DaoHelper.read(connection, sql, allColumns, supplier);
        return DaoHelper.fromSingletonList(items, "");
    }

    @Override
    public List<T> selectMany(List<Long> ids) {
        String sql = DaoHelper.baseSelectSql(tableName, dataColumns);
        List<String> idStrings = ids.stream().map(l -> l.toString()).collect(Collectors.toList());
        String idsString = String.join(",", idStrings);
        sql = sql + " where " + primaryKey.keyName() + " in (" + idsString + ")";
        return DaoHelper.read(connection, sql, dataColumns, supplier);
    }


    @Override
    public List<T> selectAll() {
        String sql = DaoHelper.baseSelectSql(tableName, dataColumns);
        List<T> items = DaoHelper.read(connection, sql, dataColumns, supplier);
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
        buf.append(DaoHelper.joinSelectSql(tableName, dataColumns, joinColumns));
        buf.append(" and ");
        for(int idx=0 ; idx < columnNames.size() ; idx++ ){
            String columnName = columnNames.get(idx);
            TypedColumn<T> column = columnMap.get(columnName);
            buf.append("a" + "." + column.getName());
            buf.append(" = ?");
            if( idx < columnNames.size() - 1 ) {
                buf.append(" and ");
            }
        }
        String sql = buf.toString();

        System.out.println("SQL " + sql);

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            for(int idx=0 ; idx < columnNames.size() ; idx++ ){
                String columnName = columnNames.get(idx);
                TypedColumn<T> column = columnMap.get(columnName);
                column.setValue(item, idx + 1, statement);
            }
            ResultSet resultSet = statement.executeQuery();
            List<T> items = new ArrayList<>();

            while (resultSet.next()) {
                T t = DaoHelper.populate("", resultSet, supplier, dataColumns, joinColumns);
                items.add(t);
            }

            return items;
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
}
