package org.center4racialjustice.legup.util;

import java.util.ArrayList;
import java.util.List;
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
        return new Tuple(matched, unMatched);
    }
}
