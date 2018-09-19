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

    private static boolean h2Initialized = false;
    private static String h2ConnectionUrl = "jdbc:h2:./db/legup_h2_test";

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
            if ( !h2Initialized) {
                cleanUpOldDb();
                initializeDb();
                h2Initialized = true;
            }
            return DriverManager.getConnection(h2ConnectionUrl);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void cleanUpOldDb(){
        try {
            Path path = Paths.get("./db/legup_h2_test.mv.db");
            Files.deleteIfExists(path);
            path = Paths.get("./db/legup_h2_test.trace.db");
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
                // we omit some lines from the creation script
                // for the h2 tests, we do not care about permissions
                // and h2 does not support uniqueness constraints
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
            Connection connection = DriverManager.getConnection(h2ConnectionUrl);
            Statement statement = connection.createStatement();
            statement.execute(wholeFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
