package org.center4racialjustice.legup.db;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.ChamberConverter;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChamberTypeHandler extends BaseTypeHandler<Chamber> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Chamber chamber, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public Chamber getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String value = resultSet.getString(s);
        return ChamberConverter.INSTANCE.fromCode(value);
    }

    @Override
    public Chamber getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String value = resultSet.getString(i);
        return ChamberConverter.INSTANCE.fromCode(value);
    }

    @Override
    public Chamber getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String value = callableStatement.getString(i);
        return ChamberConverter.INSTANCE.fromCode(value);
    }
}
