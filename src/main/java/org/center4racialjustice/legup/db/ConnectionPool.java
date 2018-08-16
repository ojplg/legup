package org.center4racialjustice.legup.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPool {

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/legup", "legupuser", "legupuserpass");
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

}
