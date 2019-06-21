package org.center4racialjustice.legup.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class H2ConnectionFactory implements ConnectionFactory {

    public static final String H2ConnectionUrl = "jdbc:h2:./db/legup_h2_test";

    private boolean h2Initialized = false;
    private String h2ConnectionUrl;

    public H2ConnectionFactory(){
        this.h2ConnectionUrl = H2ConnectionUrl;
    }

    public Connection connect(){
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

    private void cleanUpOldDb(){
        try {
            Path path = Paths.get("./db/legup_h2_test.mv.db");
            Files.deleteIfExists(path);
            path = Paths.get("./db/legup_h2_test.trace.db");
            Files.deleteIfExists(path);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void initializeDb(){
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
                        || line.matches("alter table \\w+ add constraint \\w+ unique.*")
                        || line.matches("alter table \\w+ drop constraint \\w+;")){
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
