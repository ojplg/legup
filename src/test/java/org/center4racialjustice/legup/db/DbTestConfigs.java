package org.center4racialjustice.legup.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DbTestConfigs {

    private static boolean initialized = false;
    private static String connectionUrl = "jdbc:h2:./db/legup_h2_test";

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
        try {
            if ( ! initialized ) {
                cleanUpOldDb();
                initializeDb();
                initialized = true;
            }
            return DriverManager.getConnection(connectionUrl);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void cleanUpOldDb(){
        try {
            Path path = Paths.get("./db/legup_h2_test.mv.db");
            Files.deleteIfExists(path);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private static void initializeDb(){
        try {
            Path path = Paths.get("./db/create_structures.sql");
            List<String> lines = Files.readAllLines(path);

            StringBuilder wholeFileBuffer = new StringBuilder();
            for( String line : lines){
                if(line.startsWith("begin;")
                        || line.startsWith("grant all")
                        || line.startsWith("end;")
                        || line.matches("alter table \\w+ add constraint \\w+ unique.*")){
                    continue;
                }
                wholeFileBuffer.append(line);
                wholeFileBuffer.append("\n");
            }
            String wholeFile = wholeFileBuffer.toString();

            Class.forName("org.h2.Driver" );
            Connection connection = DriverManager.getConnection(connectionUrl);
            Statement statement = connection.createStatement();
            statement.execute(wholeFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }



}
