package org.center4racialjustice.legup.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Lists {

    public static <T> Tuple<List<T>, List<T>> divide(List<T> original, Predicate<T> predicate){
        List<T> matched = new ArrayList<>();
        List<T> unMatched = new ArrayList<>();
        for(T item : original){
            if( predicate.test(item)){
                matched.add(item);
            }else {
                unMatched.add(item);
            }
        }
        return new Tuple<>(matched, unMatched);
    }

    public static <K,V> Map<K, V> asMap(List<V> items, Function<V, K> keyFunction){
        Map<K, V> map = new HashMap<>();
        if ( items == null ){
            return map;
        }
        for(V item : items){
            if (item == null ){
                continue;
            }
            K key = keyFunction.apply(item);
            map.put(key, item);
        }
        return map;
    }

    public static <T> T findfirst(List<T> list, Predicate<T> predicate){
        for(T item : list){
            if( predicate.test(item)){
                return item;
            }
        }
        return null;
    }

    public static <T> List<T> filter(List<T> items, Predicate<T> predicate){
        return items.stream().filter(predicate).collect(Collectors.toList());
    }


    public static <T,M> List<M> map(List<T> items, Function<T,M> mapper){
        return items.stream().map(mapper).collect(Collectors.toList());
    }

}
