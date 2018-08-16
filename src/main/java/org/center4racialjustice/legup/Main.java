package org.center4racialjustice.legup;

import org.apache.velocity.app.Velocity;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.web.ServerStarter;

import java.util.Properties;

public class Main {

    public static void main(String[] args){
        System.out.println("start me up");

        try {
            // make sure postgres drivers are loaded
            Class.forName("org.postgresql.Driver");

            ConnectionPool pool = new ConnectionPool(
                    "jdbc:postgresql://localhost:5432/legup",
                    "legupuser",
                    "legupuserpass"
            );

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
