package org.center4racialjustice.legup.db.hrorm;

import java.util.List;

public interface
Dao<T> {

    void insert(T item);
    void update(T item);
    void delete(T item);
    T select(long id);
    List<T> selectAll();
    
}
