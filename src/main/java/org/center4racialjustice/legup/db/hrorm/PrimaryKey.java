package org.center4racialjustice.legup.db.hrorm;

public interface PrimaryKey<T> {

    Long getKey(T item);
    String getSequenceName();
    void setKey(T item, Long id);
    String keyName();

}
