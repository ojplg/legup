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
        return selectByColumns(sql, supplier, Collections.emptyList(), Collections.emptyMap(), childrenDescriptors, null);
    }

    public List<T> selectByColumns(String sql, Supplier<T> supplier, List<String> columnNames, Map<String, TypedColumn<T>> columnNameMap,  List<ChildrenDescriptor<T,?>> childrenDescriptors, T item){
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            int idx = 1;
            for(String columnName : columnNames){
                TypedColumn<T> column = columnNameMap.get(columnName);
                column.setValue(item, idx, statement);
                idx++;
            }

            resultSet = statement.executeQuery();

            List<T> results = new ArrayList<>();

            while (resultSet.next()) {
                T result = populate(resultSet, supplier);
                for(ChildrenDescriptor<T,?> descriptor : childrenDescriptors){
                    descriptor.populateChildren(connection, result);
                }
                results.add(result);
            }

            return results;

        } catch (SQLException ex){
            throw new HrormException(ex, sql);
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

    public void insert(String sql, T item) {
        runInsertOrUpdate(sql, item, false);
    }

    public void update(String sql, T item) {
        runInsertOrUpdate(sql, item, true);
    }

    private void runInsertOrUpdate(String sql, T item, boolean isUpdate){
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            int idx = 1;
            for(TypedColumn<T> column : allColumns){
                if( ! ( isUpdate && column.isPrimaryKey() ) ) {
                    column.setValue(item, idx, preparedStatement);
                    idx++;
                }
            }

            preparedStatement.execute();

        } catch (SQLException se){
            throw new HrormException(se, sql);
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
