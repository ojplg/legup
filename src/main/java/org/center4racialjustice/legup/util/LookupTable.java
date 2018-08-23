package org.center4racialjustice.legup.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;

public class LookupTable<R, C, V> {

    private final Map<R, Map<C,V>> map = new HashMap<>();
    private final Set<R> rowHeadings = new HashSet<>();
    private final Set<C> columnHeadings = new HashSet<>();

    public V get(R row, C column){
        Map<C,V> rowMap = map.get(row);
        if (rowMap == null){
            return null;
        }
        return rowMap.get(column);
    }

    public void put(R row, C column, V value){
        rowHeadings.add(row);
        columnHeadings.add(column);
        Map<C,V> rowMap = map.get(row);
        if (rowMap == null){
            rowMap = new HashMap<>();
            map.put(row, rowMap);
        }
        rowMap.put(column, value);
    }

    public Set<R> getRowHeadings(){
        return rowHeadings;
    }

    public Set<C> getColumnHeadings(){
        return columnHeadings;
    }

    public V computeRowSummary(R row, V identity, BinaryOperator<V> accumulator){
        Map<C, V> rowMap = map.get(row);
        if( rowMap == null ){
            return null;
        }
        Collection<V> values = rowMap.values();
        return values.stream().reduce(identity, accumulator);
    }
}
