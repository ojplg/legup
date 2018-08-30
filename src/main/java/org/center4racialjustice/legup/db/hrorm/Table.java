package org.center4racialjustice.legup.db.hrorm;

import lombok.Data;

import java.util.List;

@Data
public class Table {

    private final String name;
    private final List<Column> columns;

}
