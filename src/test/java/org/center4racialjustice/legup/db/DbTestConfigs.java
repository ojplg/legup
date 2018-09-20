package org.center4racialjustice.legup.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbTestConfigs {

    public static Connection connect(){
        return getH2Connection();
    }

    public static Connection getPostgresConnection(){
        try {
            Class.forName("org.postgresql.Driver");

            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/legup_test","legupuser", "legupuserpass");

        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Connection getH2Connection(){
        H2ConnectionFactory h2ConnectionFactory = new H2ConnectionFactory();
        return h2ConnectionFactory.connect();
    }


}
