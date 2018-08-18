package org.center4racialjustice.legup.db;

public interface ColumnDescription {

    String getName();
    ColumnType getColumnType();
    boolean isReference();
    String getPrefix();

}
