package org.center4racialjustice.legup.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

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
}
