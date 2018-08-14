package org.center4racialjustice.legup.db;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestLegislatorDao {

    private static Connection connect(){
        try {
            Class.forName("org.postgresql.Driver");

            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/legup","legupuser", "legupuserpass");

        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void setUp() throws SQLException {
        clearTables();
    }

    private static void clearTables() throws SQLException {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        statement.execute("delete from legislators");
        statement.execute("delete from persons");
        statement.close();
        connection.close();
    }

    @Test
    public void testSaveAndFind(){
        Person herbie = new Person();
        herbie.setFirstName("Herbietta");
        herbie.setLastName("Johnson-McGee");
        Legislator legislator = new Legislator();
        legislator.setPerson(herbie);
        legislator.setDistrict(9L);
        legislator.setYear(2018L);
        legislator.setParty("Democrat");
        legislator.setAssembly("House");

        LegislatorDao dao = new LegislatorDao(connect());

        long id = dao.save(legislator);

        Legislator fromDB = dao.read(id);

        Assert.assertNotNull(fromDB);
        Assert.assertEquals(2018L, (long) fromDB.getYear());
        Assert.assertEquals("Democrat", fromDB.getParty());
        Assert.assertEquals("Herbietta", fromDB.getPerson().getFirstName());
    }

}
