package org.center4racialjustice.legup.db.hrorm;

public interface Converter<CLASS,CODE> {

    CODE from(CLASS item);
    CLASS to(CODE code);

}
