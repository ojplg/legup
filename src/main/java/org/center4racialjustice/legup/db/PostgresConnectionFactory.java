package org.center4racialjustice.legup.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnectionFactory implements ConnectionFactory {
    private final String url;
    private final String user;
    private final String password;

    public PostgresConnectionFactory(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex){
            throw new RuntimeException(ex);
        }
    }

    public Connection connect(){
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
}
