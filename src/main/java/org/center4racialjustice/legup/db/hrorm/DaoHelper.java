package org.center4racialjustice.legup.db.hrorm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
            return resultSet.getLong(1);
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
}
