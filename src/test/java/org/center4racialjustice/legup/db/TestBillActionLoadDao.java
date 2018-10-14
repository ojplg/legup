package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Chamber;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestBillActionLoadDao {

    @Before
    @After
    public void setUp() throws SQLException {
        clearTables();
    }

    private static void clearTables() throws SQLException {
        Connection connection = DbTestConfigs.connect();
        Statement statement = connection.createStatement();
        statement.execute("delete from report_card_legislators");
        statement.execute("delete from report_factors");
        statement.execute("delete from bill_action_loads");
        statement.execute("delete from bill_action_loads");
        statement.execute("delete from bills");
        connection.commit();
        statement.close();
        connection.close();
    }

    @Test
    public void testSelect() throws SQLException {

        Connection connection = DbTestConfigs.connect();

        Bill bill1 = new Bill();
        bill1.setChamber(Chamber.House);
        bill1.setNumber(1);

        BillDao billDao = new BillDao(connection);
        billDao.insert(bill1);

        BillActionLoad billActionLoad1 = new BillActionLoad();
        billActionLoad1.setBill(bill1);
        billActionLoad1.setUrl("url1");
        billActionLoad1.setCheckSum(8811L);
        billActionLoad1.setLoadTime(LocalDateTime.now());

        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
        billActionLoadDao.insert(billActionLoad1);

        connection.commit();

        BillActionLoad load = billActionLoadDao.select(billActionLoad1.getId());

        Assert.assertEquals(bill1, load.getBill());
    }


    @Test
    public void testSelectAll() throws SQLException {

        Connection connection = DbTestConfigs.connect();

        Bill bill1 = new Bill();
        bill1.setChamber(Chamber.House);
        bill1.setNumber(1);

        Bill bill2 = new Bill();
        bill2.setChamber(Chamber.House);
        bill2.setNumber(2);

        BillDao billDao = new BillDao(connection);
        billDao.insert(bill1);
        billDao.insert(bill2);

        BillActionLoad billActionLoad1 = new BillActionLoad();
        billActionLoad1.setBill(bill1);
        billActionLoad1.setUrl("url1");
        billActionLoad1.setCheckSum(8811L);
        billActionLoad1.setLoadTime(LocalDateTime.now());

        BillActionLoad billActionLoad2 = new BillActionLoad();
        billActionLoad2.setBill(bill2);
        billActionLoad2.setUrl("url2");
        billActionLoad2.setCheckSum(8822L);
        billActionLoad2.setLoadTime(LocalDateTime.now());

        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
        billActionLoadDao.insert(billActionLoad1);
        billActionLoadDao.insert(billActionLoad2);

        connection.commit();

        List<BillActionLoad> loads = billActionLoadDao.selectAll();

        Assert.assertEquals(2, loads.size());
        Set<Bill> foundBills = loads.stream().map(BillActionLoad::getBill).collect(Collectors.toSet());

        Assert.assertTrue(foundBills.contains(bill1));
        Assert.assertTrue(foundBills.contains(bill2));

    }


    @Test
    public void testSelectMany() throws SQLException {
        Connection connection = DbTestConfigs.connect();

        Bill bill1 = new Bill();
        bill1.setChamber(Chamber.House);
        bill1.setNumber(1);

        Bill bill2 = new Bill();
        bill2.setChamber(Chamber.House);
        bill2.setNumber(2);

        BillDao billDao = new BillDao(connection);
        billDao.insert(bill1);
        billDao.insert(bill2);

        BillActionLoad billActionLoad1 = new BillActionLoad();
        billActionLoad1.setBill(bill1);
        billActionLoad1.setUrl("url1");
        billActionLoad1.setCheckSum(8811L);
        billActionLoad1.setLoadTime(LocalDateTime.now());

        BillActionLoad billActionLoad2 = new BillActionLoad();
        billActionLoad2.setBill(bill2);
        billActionLoad2.setUrl("url2");
        billActionLoad2.setCheckSum(8822L);
        billActionLoad2.setLoadTime(LocalDateTime.now());

        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
        billActionLoadDao.insert(billActionLoad1);
        billActionLoadDao.insert(billActionLoad2);

        connection.commit();

        List<BillActionLoad> loads = billActionLoadDao.selectMany(Arrays.asList(billActionLoad1.getId(), billActionLoad2.getId()));

        Assert.assertEquals(2, loads.size());
        Set<Bill> foundBills = loads.stream().map(BillActionLoad::getBill).collect(Collectors.toSet());

        Assert.assertTrue(foundBills.contains(bill1));
        Assert.assertTrue(foundBills.contains(bill2));

    }

}
