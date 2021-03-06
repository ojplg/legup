package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class TestLegislatorDao {

    static {
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }

    @Before
    @After
    public void setUp() throws SQLException {
        clearTables();
    }

    private static void clearTables() throws SQLException {
        Connection connection = DbTestConfigs.connect();
        Statement statement = connection.createStatement();
        statement.execute("delete from legislators");
        connection.commit();
        statement.close();
        connection.close();
    }

    @Test
    public void testSavingCuriousCharacters(){
        Legislator legislator = new Legislator();
        legislator.setFirstName("André");
        legislator.setLastName("Thapedi");
        legislator.setDistrict(9L);
        legislator.setSessionNumber(2018L);
        legislator.setParty("Democrat");
        legislator.setChamber(Chamber.House);

        LegislatorDao dao = new LegislatorDao(DbTestConfigs.connect());

        long id = dao.insert(legislator);

        Legislator fromDB = dao.read(id);

        Assert.assertNotNull(fromDB);
        Assert.assertEquals("André", fromDB.getFirstName());
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

        LegislatorDao dao = new LegislatorDao(DbTestConfigs.connect());

        long id = dao.insert(legislator);

        Legislator fromDB = dao.read(id);

        Assert.assertNotNull(fromDB);
        Assert.assertEquals(2018L, fromDB.getSessionNumber());
        Assert.assertEquals("Democrat", fromDB.getParty());
        Assert.assertEquals("Herbietta", fromDB.getFirstName());
    }

    @Test
    public void testReadAll() throws SQLException {
        Connection connection = DbTestConfigs.connect();

        Legislator legislator = new Legislator();
        legislator.setFirstName("Herbietta");
        legislator.setLastName("Johnson-McGee");
        legislator.setDistrict(9L);
        legislator.setSessionNumber(2018L);
        legislator.setParty("Democrat");
        legislator.setChamber(Chamber.House);

        LegislatorDao dao = new LegislatorDao(connection);

        dao.insert(legislator);

        Legislator legislator2 = new Legislator();
        legislator2.setFirstName("Mister");
        legislator2.setLastName("Baleen");
        legislator2.setDistrict(1L);
        legislator2.setSessionNumber(2018L);
        legislator2.setParty("Democrat");
        legislator2.setChamber(Chamber.House);

        dao.insert(legislator2);

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

        LegislatorDao dao = new LegislatorDao(DbTestConfigs.connect());

        long id = dao.insert(legislator);

        Legislator fromDB = dao.read(id);

        Assert.assertNotNull(fromDB);
        Assert.assertEquals(100L, fromDB.getSessionNumber());
        Assert.assertEquals(Chamber.House, fromDB.getChamber());
        Assert.assertEquals("Democrat", fromDB.getParty());
        Assert.assertEquals("Herbietta", fromDB.getFirstName());

        legislator.setId(id);
        legislator.setSessionNumber(101L);
        legislator.setDistrict(5L);
        legislator.setChamber(Chamber.Senate);

        dao.update(legislator);

        Legislator secondFromDB = dao.read(id);

        Assert.assertNotNull(fromDB);
        Assert.assertEquals(101L, secondFromDB.getSessionNumber());
        Assert.assertEquals(Chamber.Senate, secondFromDB.getChamber());
        Assert.assertEquals("Democrat", secondFromDB.getParty());
        Assert.assertEquals("Herbietta", secondFromDB.getFirstName());
    }

    @Test
    public void testReadByMemberId(){
        Legislator legislator = new Legislator();
        legislator.setFirstName("Herbietta");
        legislator.setLastName("Johnson-McGee");
        legislator.setDistrict(9L);
        legislator.setSessionNumber(100L);
        legislator.setParty("Democrat");
        legislator.setChamber(Chamber.House);
        legislator.setMemberId("123");

        LegislatorDao dao = new LegislatorDao(DbTestConfigs.connect());

        dao.insert(legislator);

        Legislator readLeg = dao.readByMemberId("123");

        Assert.assertNotNull(readLeg);

    }

}
