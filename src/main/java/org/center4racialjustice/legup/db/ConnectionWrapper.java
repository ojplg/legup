package org.center4racialjustice.legup.db;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionWrapper implements Closeable {

    private final Connection connection;

    public ConnectionWrapper(Connection connection){
        this.connection = connection;
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    public Connection getConnection(){
        return connection;
    }

    public void rollback(){
        try {
            this.connection.rollback();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    public void commit(){
        try {
            this.connection.commit();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    public void close(){
        try {
            if (connection != null){
                connection.close();
            }
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
}
