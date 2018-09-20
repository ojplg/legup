package org.center4racialjustice.legup.db;

import java.sql.Connection;

public class DbTestConfigs {

    public static Connection connect(){
        return getH2Connection();
    }

    public static Connection getPostgresConnection(){
        PostgresConnectionFactory connectionFactory = new PostgresConnectionFactory(
                "jdbc:postgresql://localhost:5432/legup_test","legupuser", "legupuserpass");

        return connectionFactory.connect();
    }

    public static Connection getH2Connection(){
        H2ConnectionFactory h2ConnectionFactory = new H2ConnectionFactory();
        return h2ConnectionFactory.connect();
    }

}
