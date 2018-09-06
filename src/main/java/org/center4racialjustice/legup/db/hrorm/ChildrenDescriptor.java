package org.center4racialjustice.legup.db.hrorm;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ChildrenDescriptor<T,U> {
    private final String parentChildColumnName;
    private final Function<T, List<U>> getter;
    private final BiConsumer<T, List<U>> setter;
    private final DaoDescriptor<U> daoDescriptor;
    private final PrimaryKey<T> primaryKey;
    private final BiConsumer<U, Long> parentSetter;

    public ChildrenDescriptor(String parentChildColumnName, BiConsumer<U, Long> parentSetter,
                              Function<T, List<U>> getter, BiConsumer<T, List<U>> setter, DaoDescriptor<U> daoDescriptor,
                              PrimaryKey<T> primaryKey) {
        this.parentChildColumnName = parentChildColumnName;
        this.getter = getter;
        this.setter = setter;
        this.daoDescriptor = daoDescriptor;
        this.primaryKey = primaryKey;
        this.parentSetter = parentSetter;
    }

    public void populateChildren(Connection connection, T item){

        System.out.println("YES!");

        String sql = DaoHelper.selectByColumns(daoDescriptor.tableName(), daoDescriptor.dataColumns(), daoDescriptor.joinColumns(), Collections.singletonList(parentChildColumnName));
        System.out.println("SQL! " + sql);
        U key = daoDescriptor.supplier().get();
        Long id = primaryKey.getKey(item);
        parentSetter.accept(key, id);
        List<U> children = DaoHelper.runSelectByColumns(connection, sql, daoDescriptor.supplier(), daoDescriptor.dataColumns(), daoDescriptor.joinColumns(),
                Collections.singletonList(parentChildColumnName), key);
        System.out.println("Children " + children.size());
        setter.accept(item, children);
    }
}
