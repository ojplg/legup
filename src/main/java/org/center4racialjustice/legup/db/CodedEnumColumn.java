package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.CodedEnumConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CodedEnumColumn<T, E> implements TypedColumn<T> {

    private final String name;
    private final String prefix;
    private final BiConsumer<T, E> setter;
    private final Function<T, E> getter;
    private final CodedEnumConverter<E> enumConverter;

    public CodedEnumColumn(String name, String prefix, Function<T, E> getter, BiConsumer<T, E> setter, CodedEnumConverter<E> enumConverter) {
        this.name = name;
        this.prefix = prefix;
        this.getter = getter;
        this.setter = setter;
        this.enumConverter = enumConverter;
    }

    @Override
    public TypedColumn<T> withPrefix(String prefix) {
        return new CodedEnumColumn<>(name, prefix, getter, setter, enumConverter);
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
        String code = resultSet.getString(prefix + name);
        E value = enumConverter.fromCode(code);
        setter.accept(item, value);
    }

    @Override
    public void setValue(T item, int index, PreparedStatement preparedStatement) throws SQLException {
        E value = getter.apply(item);
        String code = enumConverter.toCode(value);
        preparedStatement.setString(index, code);
    }
}
