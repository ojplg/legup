package org.center4racialjustice.legup.db.hrorm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LocalDateTimeColumn<T> implements TypedColumn<T> {

    private final String name;
    private final String prefix;
    private final BiConsumer<T, LocalDateTime> setter;
    private final Function<T, LocalDateTime> getter;
    private final ZoneId zoneId;

    public LocalDateTimeColumn(String name, String prefix, Function<T, LocalDateTime> getter, BiConsumer<T, LocalDateTime> setter) {
        this.name = name;
        this.prefix = prefix;
        this.getter = getter;
        this.setter = setter;
        // FIXME: Needs to be settable and default to UTC
        this.zoneId = ZoneId.of("America/Chicago");
    }

    @Override
    public TypedColumn<T> withPrefix(String prefix) {
        return new LocalDateTimeColumn<>(name, prefix, getter, setter);
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
        Timestamp sqlTime = resultSet.getTimestamp(prefix + name);
        LocalDateTime value = sqlTime.toLocalDateTime();
        setter.accept(item, value);
    }

    @Override
    public void setValue(T item, int index, PreparedStatement preparedStatement) throws SQLException {
        LocalDateTime value = getter.apply(item);
        ZoneOffset zoneOffset = zoneId.getRules().getOffset(value);
        Timestamp sqlTime = Timestamp.from(value.toInstant(zoneOffset));
        preparedStatement.setTimestamp(index, sqlTime);
    }
}



























