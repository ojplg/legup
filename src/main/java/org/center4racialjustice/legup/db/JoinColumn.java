package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Identifiable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JoinColumn<T, J extends Identifiable> implements TypedColumn<T> {

    private final String name;
    private final String prefix;
    private final BiConsumer<T, J> setter;
    private final Function<T, J> getter;
    private final Supplier<J> supplier;
    private final List<TypedColumn<J>> columnList;

    public JoinColumn(String name, String prefix, Function<T, J> getter, BiConsumer<T, J> setter, Supplier<J> supplier, List<TypedColumn<J>> columnList) {
        this.name = name;
        this.prefix = prefix;
        this.getter = getter;
        this.setter = setter;
        this.supplier = supplier;
        this.columnList = columnList;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void populate(T item, ResultSet resultSet) throws SQLException {
        J joined  = supplier.get();
        for (TypedColumn<J> column: columnList) {
            column.populate(joined, resultSet);
        }

        setter.accept(item, joined);
    }

    @Override
    public void setValue(T item, int index, PreparedStatement preparedStatement) throws SQLException {
        J value = getter.apply(item);
        Long id = value.getId();
        preparedStatement.setLong(index, id);
    }
}
