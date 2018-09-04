package org.center4racialjustice.legup.db.hrorm;

import java.util.List;

public interface Dao<T> {

    long insert(T item);
    void update(T item);
    void delete(T item);
    T select(long id);
    List<T> selectMany(List<Long> ids);
    List<T> selectAll();
    T selectByColumns(T item, List<String> columnName);
    List<T> selectManyByColumns(T item, List<String> columnNames);

}
