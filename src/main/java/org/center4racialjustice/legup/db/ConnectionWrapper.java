package org.center4racialjustice.legup.db;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionWrapper implements Closeable {

    private final Connection connection;

    public ConnectionWrapper(Connection connection){
        this.connection = connection;
    }

    public Connection getConnection(){
        return connection;
    }

    public void close(){
        try {
            if (connection != null){
                connection.close();
            }
        } catch (SQLException ex){
            throw new RuntimeException();
        }
    }
}
