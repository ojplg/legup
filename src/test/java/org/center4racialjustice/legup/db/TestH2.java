package org.center4racialjustice.legup.db;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class TestH2 {

    private static boolean initialized = false;
    private static String connectionUrl = "jdbc:h2:./db/legup_h2_test";

    public static Connection getConnection(){
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

    @Test
    public void testPullFromSequence(){

        try {
            String selectSql = "select nextval('legislator_seq')";

            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            boolean selected = statement.execute(selectSql);
            Assert.assertTrue(selected);

            ResultSet resultSet = statement.getResultSet();
            resultSet.next();
            long pull1 = resultSet.getLong(1);

            Assert.assertTrue(pull1 > 0);

            resultSet.close();
            statement.close();
            connection.close();

            connection = getConnection();
            statement = connection.createStatement();

            statement.execute(selectSql);

            resultSet = statement.getResultSet();
            resultSet.next();
            long pull2 = resultSet.getLong(1);

            Assert.assertTrue(pull2 > pull1);
        } catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }



    }

}
