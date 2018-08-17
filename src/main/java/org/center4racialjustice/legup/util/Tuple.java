package org.center4racialjustice.legup.util;

import lombok.Data;

@Data
public class Tuple<F,S> {
    private final F first;
    private final S second;
}
