package org.center4racialjustice.legup.db.hrorm;

public interface PrimaryKey<T> {

    Long getKey(T item);
    void setKey(T item, Long id);

}
