package org.center4racialjustice.legup.db;

import java.util.List;

public interface Dao<T extends Identifiable> {

    long save(T item);
    T read(long id);
    List<T> readAll();
}
