package org.center4racialjustice.legup;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.app.Velocity;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.MybatisStarter;
import org.center4racialjustice.legup.web.ServerStarter;

import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    private static final Properties properties = new Properties();

    public static void main(String[] args){
        log.info("Starting");

        try {
            if (args.length > 0){
                log.info("Loading properies from " + args[0]);
                Reader reader = new FileReader(args[0]);
                properties.load(reader);
            } else {
                log.info("Using default properties");
                properties.setProperty("db.name", "legup");
                properties.setProperty("db.url","jdbc:postgresql://localhost:5432/legup");
                properties.setProperty("db.user","legupuser");
                properties.setProperty("db.password","legupuserpass");
            }

            // make sure postgres drivers are loaded
            Class.forName("org.postgresql.Driver");

            ConnectionPool pool = new ConnectionPool(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password")
            );

            MybatisStarter mybatisStarter = new MybatisStarter(
                    properties.getProperty("db.name"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password")
            );

            SqlSessionFactory sqlSessionFactory = mybatisStarter.sessionFactory();
            pool.setSqlSessionFactory(sqlSessionFactory);

            Properties velocityProperties = new Properties();
            velocityProperties.put("resource.loader", "class");
            velocityProperties.put("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init(velocityProperties);

            ServerStarter serverStarter = new ServerStarter(pool);
            serverStarter.start();
        } catch (Exception ex){
            System.out.println("Could not start server");
            ex.printStackTrace();
        }
    }
}
