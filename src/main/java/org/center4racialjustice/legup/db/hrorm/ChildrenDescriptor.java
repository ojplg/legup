package org.center4racialjustice.legup.db.hrorm;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        SortedMap<String, TypedColumn<U>> columnNameMap = daoDescriptor.columnMap(Collections.singletonList(parentChildColumnName));
        String sql = DaoHelper.selectByColumns(daoDescriptor.tableName(), daoDescriptor.dataColumns(),
                daoDescriptor.joinColumns(), columnNameMap);
        U key = daoDescriptor.supplier().get();
        Long id = primaryKey.getKey(item);
        parentSetter.accept(key, id);
        List<U> children = DaoHelper.runSelectByColumns(connection, sql, daoDescriptor.supplier(), daoDescriptor.dataColumns(), daoDescriptor.joinColumns(),
                columnNameMap, key);
        setter.accept(item, children);
    }

    public void saveChildren(Connection connection, T item){
        List<U> children = getter.apply(item);
        List<Long> goodChildrenIds = new ArrayList<>();
        Long parentId = primaryKey.getKey(item);
        for(U child : children){
            parentSetter.accept(child, parentId);
            if( daoDescriptor.primaryKey().getKey(child) == null ) {
                Long id = DaoHelper.doInsert(connection, daoDescriptor.tableName(), daoDescriptor.dataColumns(), daoDescriptor.joinColumns(), child);
                goodChildrenIds.add(id);
            } else {
                DaoHelper.doUpdate(connection, daoDescriptor.tableName(), daoDescriptor.dataColumns(), daoDescriptor.joinColumns(), daoDescriptor.primaryKey(), child);
                goodChildrenIds.add(daoDescriptor.primaryKey().getKey(child));
            }
        }
        deleteOrphans(connection, item, goodChildrenIds);
    }

    private void deleteOrphans(Connection connection, T item, List<Long> goodChildrenIds) {

        StringBuilder buf = new StringBuilder();
        buf.append("delete from ");
        buf.append(daoDescriptor.tableName());
        buf.append(" where ");
        buf.append(parentChildColumnName);
        buf.append(" = ");
        buf.append(primaryKey.getKey(item));

        if( goodChildrenIds.size() > 0 ) {
            List<String> goodChildrenIdStrings = goodChildrenIds.stream().map(Object::toString).collect(Collectors.toList());

            buf.append(" and ");
            buf.append(daoDescriptor.primaryKey().keyName());
            buf.append(" not in ");
            buf.append("(");
            buf.append(String.join(", ", goodChildrenIdStrings));
            buf.append(")");
        }

        String sql = buf.toString();

        DaoHelper.runDelete(connection, sql);
    }

}
