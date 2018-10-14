package org.center4racialjustice.legup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.app.Velocity;
import org.center4racialjustice.legup.db.ConnectionFactory;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.H2ConnectionFactory;
import org.center4racialjustice.legup.db.PostgresConnectionFactory;
import org.center4racialjustice.legup.domain.NameOverrides;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.web.ServerStarter;

import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

public class Main {

    private static final Properties properties = new Properties();
    private static Logger log;

    static {
        // We need to set this property before we even instantiate a single logger
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
        log = LogManager.getLogger(Main.class);
    }

    public static void main(String[] args){

        log.info("Starting");

        try {
            if (args.length > 0){
                log.info("Loading properies from " + args[0]);
                Reader reader = new FileReader(args[0]);
                properties.load(reader);
            } else {
                log.info("Using default properties");
                properties.setProperty("db.url","jdbc:postgresql://localhost:5432/legup");
                properties.setProperty("db.user","legupuser");
                properties.setProperty("db.password","legupuserpass");
                properties.setProperty("db.h2", "false");
            }

            ConnectionFactory connectionFactory = getConnectionFactory(properties);
            ConnectionPool connectionPool = new ConnectionPool(connectionFactory);

            Properties velocityProperties = new Properties();
            velocityProperties.put("resource.loader", "class");
            velocityProperties.put("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init(velocityProperties);

            NameParser nameParser = NameOverrides.loadNameParser("conf/name.overrides");

            ServerStarter serverStarter = new ServerStarter(connectionPool, nameParser);
            serverStarter.start();
        } catch (Exception ex){
            System.out.println("Could not start server");
            ex.printStackTrace();
        }
    }

    private static ConnectionFactory getConnectionFactory(Properties properties){
        if ("true".equalsIgnoreCase(properties.getProperty("db.h2"))){
            log.info("Using H2 database.");
            H2ConnectionFactory h2ConnectionFactory = new H2ConnectionFactory();
            return h2ConnectionFactory;
        } else {
            log.info("Using postgres db at " + properties.getProperty("db.url"));
            ConnectionFactory connectionFactory = new PostgresConnectionFactory(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password")
            );
            return connectionFactory;
        }
    }
}
