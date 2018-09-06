package org.center4racialjustice.legup.db.hrorm;

import java.util.List;
import java.util.function.Supplier;

public interface DaoDescriptor<T> {

    String tableName();
    Supplier<T> supplier();
    List<TypedColumn<T>> dataColumns();
    List<JoinColumn<T,?>> joinColumns();
    PrimaryKey<T> primaryKey();

}
