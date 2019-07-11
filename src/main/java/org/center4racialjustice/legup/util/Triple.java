package org.center4racialjustice.legup.util;

import lombok.Data;

@Data
public class Triple<T,U,V> {
    private final T first;
    private final U second;
    private final V third;
}
