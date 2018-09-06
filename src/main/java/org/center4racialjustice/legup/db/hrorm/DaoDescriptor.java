package org.center4racialjustice.legup.db.hrorm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

public interface DaoDescriptor<T> {

    String tableName();
    Supplier<T> supplier();
    List<TypedColumn<T>> dataColumns();
    List<JoinColumn<T,?>> joinColumns();
    PrimaryKey<T> primaryKey();

    default List<TypedColumn<T>> allColumns(){
        List<TypedColumn<T>> allColumns = new ArrayList<>();
        allColumns.addAll(dataColumns());
        allColumns.addAll(joinColumns());
        return Collections.unmodifiableList(allColumns);
    }

    default SortedMap<String, TypedColumn<T>> columnMap(Collection<String> columnNames){
        SortedMap<String, TypedColumn<T>> map = new TreeMap<>();
        for(TypedColumn<T> column : allColumns()){
            if (columnNames.contains(column.getName())) {
                map.put(column.getName(), column);
            }
        }
        return Collections.unmodifiableSortedMap(map);
    }
}
