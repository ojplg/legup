package org.center4racialjustice.legup.db.hrorm;

import lombok.Data;

@Data
public class Column {

    private final String name;
    private final ColumnType columnType;

}
