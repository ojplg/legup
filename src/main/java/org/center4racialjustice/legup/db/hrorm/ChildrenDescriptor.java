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
        String sql = DaoHelper.selectByColumns(daoDescriptor.tableName(), daoDescriptor.dataColumns(), daoDescriptor.joinColumns(), Collections.singletonList(parentChildColumnName));
        U key = daoDescriptor.supplier().get();
        Long id = primaryKey.getKey(item);
        parentSetter.accept(key, id);
        List<U> children = DaoHelper.runSelectByColumns(connection, sql, daoDescriptor.supplier(), daoDescriptor.dataColumns(), daoDescriptor.joinColumns(),
                Collections.singletonList(parentChildColumnName), key);
        System.out.println("Children " + children.size());
        setter.accept(item, children);
    }

    public void saveChildren(Connection connection, T item){
        List<U> children = getter.apply(item);
        for(U child : children){
            if( daoDescriptor.primaryKey().getKey(child) == null ) {
                DaoHelper.doInsert(connection, daoDescriptor.tableName(), daoDescriptor.dataColumns(), daoDescriptor.joinColumns(), child);
            } else {
                DaoHelper.doUpdate(connection, daoDescriptor.tableName(), daoDescriptor.dataColumns(), daoDescriptor.primaryKey(), child);
            }
        }
        // need to delete existing children that are no longer part of the parent!

    }
}
