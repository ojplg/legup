package org.center4racialjustice.legup.domain;

public interface CodedEnumConverter<T> {

    T fromCode(String code);
    String toCode(T instance);

}
