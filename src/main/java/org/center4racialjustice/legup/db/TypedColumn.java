package org.center4racialjustice.legup.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypedColumn<T> {

    String getName();
    String getPrefix();
    void populate(T item, ResultSet resultSet) throws SQLException;
    void setValue(T item, int index, PreparedStatement preparedStatement) throws SQLException;

}
