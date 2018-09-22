package org.center4racialjustice.legup.db.hrorm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DaoHelper {

    private static final Logger log = LogManager.getLogger(DaoHelper.class);

    public static void runDelete(Connection connection, String sql) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql) ){
            preparedStatement.execute();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    public static long getNextSequenceValue(Connection connection, String sequenceName) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select nextval('" + sequenceName + "')");
            resultSet.next();
            long value = resultSet.getLong(1);
            return value;
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        } finally {
            try {
                if ( resultSet != null ){
                    resultSet.close();
                }
                if ( statement != null){
                    statement.close();
                }
            } catch (SQLException ex){
                log.error("Could not close", ex);
            }
        }
    }

    public static <T> T fromSingletonList(List<T> items, String errorMsg) {
        if (items.isEmpty()) {
            return null;
        }
        if (items.size() == 1) {
            return items.get(0);
        }
        throw new RuntimeException("Found " + items.size() + " items. Message: " + errorMsg);
    }

}
