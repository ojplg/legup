package org.center4racialjustice.legup.db.hrorm;

public interface Converter<T,U> {

    T to(U u);
    U from(T t);

}
