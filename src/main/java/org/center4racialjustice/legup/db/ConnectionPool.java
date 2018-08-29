package org.center4racialjustice.legup.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPool {

    private final String url;
    private final String user;
    private final String password;

    public ConnectionPool(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection(){
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

}
