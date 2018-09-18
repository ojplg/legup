package org.center4racialjustice.legup.db;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestH2 {

    public static Connection getConnection(){
        try {

            Class.forName("org.h2.Driver" );
            Connection connection = DriverManager.getConnection("jdbc:h2:~/legup");
            return connection;
        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException(ex);
        }
    }


    @Test
    public void createStuff(){

        try {
            String createSequence = "create sequence if not exists legislator_seq";
            Connection connection = getConnection();

            Statement statement = connection.createStatement();
            boolean created = statement.execute(createSequence);

            Assert.assertFalse(created);

            statement.close();

            statement = connection.createStatement();

            String selectValue = "select nextval('legislator_seq')";

            boolean selected = statement.execute(selectValue);

            Assert.assertTrue(selected);

            ResultSet resultSet = statement.getResultSet();
            resultSet.next();
            long foo = resultSet.getLong(1);

            Assert.assertTrue(foo > 0);

        } catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }



    }

}
