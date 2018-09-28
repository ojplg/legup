package org.center4racialjustice.legup.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public class LookupTable<R, C, V> {

    private final Map<R, Map<C,V>> map = new HashMap<>();
    private final Set<R> rowHeadings = new HashSet<>();
    private final Set<C> columnHeadings = new HashSet<>();

    private final V defaultValue;

    public LookupTable(){
        this.defaultValue = null;
    }

    public LookupTable(V defaultValue){
        this.defaultValue = defaultValue;
    }

    public V get(R row, C column){
        Map<C,V> rowMap = map.get(row);
        if (rowMap == null){
            return defaultValue;
        }
        V value = rowMap.get(column);
        if( value == null ){
            return defaultValue;
        }
        return value;
    }

    public void put(R row, C column, V value){
        rowHeadings.add(row);
        columnHeadings.add(column);
        Map<C, V> rowMap = map.computeIfAbsent(row, k -> new HashMap<>());
        rowMap.put(column, value);
    }

    public V merge(R row, C column, V value, BiFunction<V,V,V> updater){
        rowHeadings.add(row);
        columnHeadings.add(column);
        Map<C, V> rowMap = map.computeIfAbsent(row, k -> new HashMap<>());
        return rowMap.merge(column, value, updater);
    }

    public Set<R> getRowHeadings(){
        return rowHeadings;
    }

    public Set<C> getColumnHeadings(){
        return columnHeadings;
    }

    public List<R> sortedRowHeadings(Comparator<R> comparator) {
        List<R> rows = new ArrayList<>(getRowHeadings());
        rows.sort(comparator);
        return rows;
    }

    public List<C> sortedColumnHeadings(Comparator<C> comparator) {
        List<C> columns = new ArrayList<>(getColumnHeadings());
        columns.sort(comparator);
        return columns;
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
