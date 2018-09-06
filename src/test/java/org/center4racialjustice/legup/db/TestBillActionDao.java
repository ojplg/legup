package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.VoteSide;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

public class TestBillActionDao {

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
    public void testReadOne() throws SQLException {

        Connection connection = DbTestConfigs.connect();

        Legislator wilson = new Legislator();
        wilson.setFirstName("Wilson");
        wilson.setChamber(Chamber.House);
        wilson.setDistrict(1);
        wilson.setSessionNumber(314);

        LegislatorDao legislatorDao = new LegislatorDao(connection);
        long wilsonId = legislatorDao.save(wilson);
        wilson.setId(wilsonId);

        Bill bill = new Bill();
        bill.setChamber(Chamber.House);
        bill.setNumber(123);

        BillDao billDao = new BillDao(connection);
        long billId = billDao.save(bill);
        bill.setId(billId);

        BillActionLoad billActionLoad = new BillActionLoad();
        billActionLoad.setBill(bill);
        billActionLoad.setUrl("url");
        billActionLoad.setCheckSum(88L);
        billActionLoad.setLoadTime(LocalDateTime.now());

        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
        long voteLoadId = billActionLoadDao.insert(billActionLoad);
        billActionLoad.setId(voteLoadId);

        Statement statement = connection.createStatement();
        String insertSql =
                "insert into bill_actions (bill_id, legislator_id, bill_action_type, bill_action_detail, bill_action_load_id) values "
                + "(" + billId + ", " + wilsonId + ", 'Vote', 'N', " + voteLoadId + ") RETURNING ID";
        ResultSet resultSet = statement.executeQuery(insertSql);
        resultSet.next();
        long voteId = resultSet.getLong("id");

        BillActionDao billActionDao = new BillActionDao(connection);

        BillAction billAction = billActionDao.read(voteId);
        Vote vote = billAction.asVote();

        Assert.assertEquals("Wilson", vote.getLegislator().getFirstName());
        Assert.assertEquals(123, vote.getBill().getNumber());
        Assert.assertEquals(VoteSide.Nay, vote.getVoteSide());
    }

    @Test
    public void testInsert() {

        Connection connection = DbTestConfigs.connect();

        Legislator wilson = new Legislator();
        wilson.setFirstName("Wilson");
        wilson.setChamber(Chamber.House);
        wilson.setDistrict(1);
        wilson.setSessionNumber(314);

        LegislatorDao legislatorDao = new LegislatorDao(connection);
        long wilsonId = legislatorDao.save(wilson);
        wilson.setId(wilsonId);

        Bill bill = new Bill();
        bill.setChamber(Chamber.House);
        bill.setNumber(123);

        BillDao billDao = new BillDao(connection);
        long billId = billDao.save(bill);
        bill.setId(billId);

        BillActionLoad billActionLoad = new BillActionLoad();
        billActionLoad.setBill(bill);
        billActionLoad.setUrl("url");
        billActionLoad.setCheckSum(88L);
        billActionLoad.setLoadTime(LocalDateTime.now());

        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
        long voteLoadId = billActionLoadDao.insert(billActionLoad);
        billActionLoad.setId(voteLoadId);

        Vote vote = new Vote();
        vote.setBill(bill);
        vote.setLegislator(wilson);
        vote.setVoteSide(VoteSide.Yea);
        vote.setBillActionLoad(billActionLoad);

        BillActionDao billActionDao = new BillActionDao(connection);

        BillAction billAction = BillAction.fromVote(vote);
        long voteId = billActionDao.insert(billAction);
        Assert.assertTrue(voteId > 0);
    }

    @Test
    public void testLoadByBill() {

        Connection connection = DbTestConfigs.connect();

        Legislator wilson = new Legislator();
        wilson.setFirstName("Wilson");
        wilson.setChamber(Chamber.House);
        wilson.setDistrict(1);
        wilson.setSessionNumber(314);

        LegislatorDao legislatorDao = new LegislatorDao(connection);
        long wilsonId = legislatorDao.save(wilson);
        wilson.setId(wilsonId);

        Bill bill = new Bill();
        bill.setChamber(Chamber.House);
        bill.setNumber(123);

        BillDao billDao = new BillDao(connection);
        long billId = billDao.save(bill);
        bill.setId(billId);

        BillActionLoad billActionLoad = new BillActionLoad();
        billActionLoad.setBill(bill);
        billActionLoad.setUrl("url");
        billActionLoad.setCheckSum(88L);
        billActionLoad.setLoadTime(LocalDateTime.now());

        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
        long voteLoadId = billActionLoadDao.insert(billActionLoad);
        billActionLoad.setId(voteLoadId);

        Vote vote = new Vote();
        vote.setBill(bill);
        vote.setLegislator(wilson);
        vote.setVoteSide(VoteSide.Yea);
        vote.setBillActionLoad(billActionLoad);

        BillActionDao billActionDao = new BillActionDao(connection);
        BillAction billAction = BillAction.fromVote(vote);

        long voteId = billActionDao.insert(billAction);
        Assert.assertTrue(voteId > 0);

        List<BillAction> readVotes = billActionDao.readByBill(bill);

        Assert.assertEquals(1, readVotes.size());
    }


}
