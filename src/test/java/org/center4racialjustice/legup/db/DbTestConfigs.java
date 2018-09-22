package org.center4racialjustice.legup.db;

import java.sql.Connection;

public class DbTestConfigs {

    private static final H2ConnectionFactory h2ConnectionFactory = new H2ConnectionFactory();
    
    private static final PostgresConnectionFactory postgresConnectionFactory = new PostgresConnectionFactory(
            "jdbc:postgresql://localhost:5432/legup_test","legupuser", "legupuserpass");

    public static Connection connect(){
//        return getPostgresConnection();
        return getH2Connection();
    }

    public static Connection getPostgresConnection(){
        return postgresConnectionFactory.connect();
    }

    public static Connection getH2Connection(){
        return h2ConnectionFactory.connect();
    }

}
