package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
        statement.execute("delete from votes");
        statement.execute("delete from legislators");
        statement.close();
        connection.close();
    }

    @Test
    public void testSaveAndFind(){
        Legislator legislator = new Legislator();
        legislator.setFirstName("Herbietta");
        legislator.setLastName("Johnson-McGee");
        legislator.setDistrict(9L);
        legislator.setSessionNumber(2018L);
        legislator.setParty("Democrat");
        legislator.setChamber(Chamber.House);

        LegislatorDao dao = new LegislatorDao(connect());

        long id = dao.save(legislator);

        Legislator fromDB = dao.read(id);

        Assert.assertNotNull(fromDB);
        Assert.assertEquals(2018L, (long) fromDB.getSessionNumber());
        Assert.assertEquals("Democrat", fromDB.getParty());
        Assert.assertEquals("Herbietta", fromDB.getFirstName());
    }

    @Test
    public void testReadAll() throws SQLException {
        Connection connection = connect();

        Legislator legislator = new Legislator();
        legislator.setFirstName("Herbietta");
        legislator.setLastName("Johnson-McGee");
        legislator.setDistrict(9L);
        legislator.setSessionNumber(2018L);
        legislator.setParty("Democrat");
        legislator.setChamber(Chamber.House);

        LegislatorDao dao = new LegislatorDao(connection);

        dao.save(legislator);

        Legislator legislator2 = new Legislator();
        legislator2.setFirstName("Mister");
        legislator2.setLastName("Baleen");
        legislator2.setDistrict(1L);
        legislator2.setSessionNumber(2018L);
        legislator2.setParty("Democrat");
        legislator2.setChamber(Chamber.House);

        dao.save(legislator2);

        List<Legislator> legs = dao.readAll();
        Assert.assertNotNull(legs);
        Assert.assertEquals(2, legs.size());
        connection.close();
    }

    @Test
    public void testAppliesUpdates(){
        Legislator legislator = new Legislator();
        legislator.setFirstName("Herbietta");
        legislator.setLastName("Johnson-McGee");
        legislator.setDistrict(9L);
        legislator.setSessionNumber(100L);
        legislator.setParty("Democrat");
        legislator.setChamber(Chamber.House);

        LegislatorDao dao = new LegislatorDao(connect());

        long id = dao.save(legislator);

        Legislator fromDB = dao.read(id);

        Assert.assertNotNull(fromDB);
        Assert.assertEquals(100L, (long) fromDB.getSessionNumber());
        Assert.assertEquals(Chamber.House, fromDB.getChamber());
        Assert.assertEquals("Democrat", fromDB.getParty());
        Assert.assertEquals("Herbietta", fromDB.getFirstName());

        legislator.setId(id);
        legislator.setSessionNumber(101L);
        legislator.setDistrict(5L);
        legislator.setChamber(Chamber.Senate);

        dao.save(legislator);

        Legislator secondFromDB = dao.read(id);

        Assert.assertNotNull(fromDB);
        Assert.assertEquals(101L, (long) secondFromDB.getSessionNumber());
        Assert.assertEquals(Chamber.Senate, secondFromDB.getChamber());
        Assert.assertEquals("Democrat", secondFromDB.getParty());
        Assert.assertEquals("Herbietta", secondFromDB.getFirstName());

    }

}
