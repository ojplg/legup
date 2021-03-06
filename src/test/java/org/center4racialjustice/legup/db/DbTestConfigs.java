package org.center4racialjustice.legup.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DbTestConfigs {

    static {
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }

    public static final String PostgresUrl = "jdbc:postgresql://localhost:5432/legup_test";
    public static final String PostgresUser = "legupuser";
    public static final String PostgresPassword = "legupuserpass";

    private static final H2ConnectionFactory h2ConnectionFactory = new H2ConnectionFactory();
    
    private static final PostgresConnectionFactory postgresConnectionFactory = new PostgresConnectionFactory(
            PostgresUrl,PostgresUser, PostgresPassword);

    public static Connection connect(){
//       return getPostgresConnection();
        return getH2Connection();
    }

    public static Connection getPostgresConnection(){
        try {
            Connection connection = postgresConnectionFactory.connect();
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    public static Connection getH2Connection(){
        return h2ConnectionFactory.connect();
    }

}
