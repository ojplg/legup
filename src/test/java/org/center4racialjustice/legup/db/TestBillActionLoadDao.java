package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.domain.VoteSide;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestBillActionLoadDao {

    @Before
    public void setUp() throws SQLException {
        clearTables();
    }

    private static void clearTables() throws SQLException {
        Connection connection = DbTestConfigs.connect();
        Statement statement = connection.createStatement();
        statement.execute("delete from bill_actions");
        statement.execute("delete from bill_action_loads");
        statement.execute("delete from report_factors");
        statement.execute("delete from bills");
        statement.execute("delete from legislators");
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
        billDao.save(bill1);

        BillActionLoad billActionLoad1 = new BillActionLoad();
        billActionLoad1.setBill(bill1);
        billActionLoad1.setUrl("url1");
        billActionLoad1.setCheckSum(8811L);
        billActionLoad1.setLoadTime(LocalDateTime.now());

        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
        billActionLoadDao.insert(billActionLoad1);

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
        billDao.save(bill1);
        billDao.save(bill2);

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

        List<BillActionLoad> loads = billActionLoadDao.selectAll();

        Assert.assertEquals(2, loads.size());
        Set<Bill> foundBills = loads.stream().map(b -> b.getBill()).collect(Collectors.toSet());

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
        billDao.save(bill1);
        billDao.save(bill2);

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

        List<BillActionLoad> loads = billActionLoadDao.selectMany(Arrays.asList(billActionLoad1.getId(), billActionLoad2.getId()));

        Assert.assertEquals(2, loads.size());
        Set<Bill> foundBills = loads.stream().map(b -> b.getBill()).collect(Collectors.toSet());

        Assert.assertTrue(foundBills.contains(bill1));
        Assert.assertTrue(foundBills.contains(bill2));

    }

}
