package org.center4racialjustice.legup.db.hrorm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Supplier;

public class SqlRunner<T> {

    private static final Logger log = LogManager.getLogger(SqlRunner.class);

    private final Connection connection;
    private final List<TypedColumn<T>> allColumns;

    public SqlRunner(Connection connection, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns) {
        this.connection = connection;
        List<TypedColumn<T>> columns = new ArrayList<>();
        columns.addAll(dataColumns);
        columns.addAll(joinColumns);
        this.allColumns = Collections.unmodifiableList(columns);
    }

    public List<T> select(String sql, Supplier<T> supplier, List<ChildrenDescriptor<T,?>> childrenDescriptors){
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {

            statement = connection.prepareStatement(sql);

            resultSet = statement.executeQuery();

            List<T> items = new ArrayList<>();

            while (resultSet.next()) {
                T item = populate(resultSet, supplier);
                for(ChildrenDescriptor<T,?> descriptor : childrenDescriptors){
                    descriptor.populateChildren(connection, item);
                }
                items.add(item);
            }

            return items;
        } catch (SQLException se){
            throw new RuntimeException(se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException se){
                log.error("Error during close",se);
            }
        }

    }

    public List<T> selectByColumns(String sql, Supplier<T> supplier, SortedMap<String, TypedColumn<T>> columnNameMap, T item){
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            int idx = 1;
            for(Map.Entry<String, TypedColumn<T>> entry : columnNameMap.entrySet()){
                entry.getValue().setValue(item, idx, statement);
                idx++;
            }
            ResultSet resultSet = statement.executeQuery();
            List<T> items = new ArrayList<>();

            while (resultSet.next()) {
                T t = populate(resultSet, supplier);
                items.add(t);
            }

            return items;
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }

    }

    public void insert(String sql, T item) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            for(int idx = 0; idx<allColumns.size(); idx++){
                TypedColumn<T> column = allColumns.get(idx);
                column.setValue(item, idx + 1, preparedStatement);
            }

            preparedStatement.execute();

        } catch (SQLException se){
            throw new RuntimeException("Wrapped SQL exception for " + sql, se);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se){
                log.error("Error during close",se);
            }
        }
    }

    public void update(String sql, T item) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            for(int idx = 1; idx<allColumns.size(); idx++){
                TypedColumn<T> column = allColumns.get(idx);
                column.setValue(item, idx, preparedStatement);
            }

            preparedStatement.execute();

        } catch (SQLException se){
            throw new RuntimeException("Wrapped SQL exception for " + sql, se);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se){
                log.error("Error during close",se);
            }
        }
    }

    private T populate(ResultSet resultSet, Supplier<T> supplier)
            throws SQLException {
        T item = supplier.get();

        for (TypedColumn<T> column: allColumns) {
            column.populate(item, resultSet);
        }

        return item;
    }

}
