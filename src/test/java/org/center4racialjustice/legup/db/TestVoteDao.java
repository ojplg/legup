package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.VoteLoad;
import org.center4racialjustice.legup.domain.VoteSide;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

public class TestVoteDao {

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
        statement.execute("delete from vote_loads");
        statement.execute("delete from report_factors");
        statement.execute("delete from bills");
        statement.execute("delete from legislators");
        statement.close();
        connection.close();
    }


    @Test
    public void testReadOne() throws SQLException {

        Connection connection = connect();

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

        VoteLoad voteLoad = new VoteLoad();
        voteLoad.setBill(bill);
        voteLoad.setUrl("url");
        voteLoad.setCheckSum(88L);
        voteLoad.setLoadTime(LocalDateTime.now());

        VoteLoadDao voteLoadDao = new VoteLoadDao(connection);
        long voteLoadId = voteLoadDao.insert(voteLoad);
        voteLoad.setId(voteLoadId);

        Statement statement = connection.createStatement();
        String insertSql =
                "insert into votes (bill_id, legislator_id, vote_side, vote_load_id) values "
                + "(" + billId + ", " + wilsonId + ", " + "'N', " + voteLoadId + ") RETURNING ID";
        ResultSet resultSet = statement.executeQuery(insertSql);
        resultSet.next();
        long voteId = resultSet.getLong("id");

        VoteDao voteDao = new VoteDao(connection);

        Vote vote = voteDao.read(voteId);
        Assert.assertEquals("Wilson", vote.getLegislator().getFirstName());
        Assert.assertEquals(123, vote.getBill().getNumber());
        Assert.assertEquals(VoteSide.Nay, vote.getVoteSide());
    }

    @Test
    public void testInsert() throws SQLException {

        Connection connection = connect();

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

        VoteLoad voteLoad = new VoteLoad();
        voteLoad.setBill(bill);
        voteLoad.setUrl("url");
        voteLoad.setCheckSum(88L);
        voteLoad.setLoadTime(LocalDateTime.now());

        VoteLoadDao voteLoadDao = new VoteLoadDao(connection);
        long voteLoadId = voteLoadDao.insert(voteLoad);
        voteLoad.setId(voteLoadId);

        Vote vote = new Vote();
        vote.setBill(bill);
        vote.setLegislator(wilson);
        vote.setVoteSide(VoteSide.Yea);
        vote.setVoteLoad(voteLoad);

        VoteDao voteDao = new VoteDao(connection);

        long voteId = voteDao.insert(vote);
        Assert.assertTrue(voteId > 0);
    }

    @Test
    public void testLoadByBill() {

        Connection connection = connect();

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

        VoteLoad voteLoad = new VoteLoad();
        voteLoad.setBill(bill);
        voteLoad.setUrl("url");
        voteLoad.setCheckSum(88L);
        voteLoad.setLoadTime(LocalDateTime.now());

        VoteLoadDao voteLoadDao = new VoteLoadDao(connection);
        long voteLoadId = voteLoadDao.insert(voteLoad);
        voteLoad.setId(voteLoadId);

        Vote vote = new Vote();
        vote.setBill(bill);
        vote.setLegislator(wilson);
        vote.setVoteSide(VoteSide.Yea);
        vote.setVoteLoad(voteLoad);

        VoteDao voteDao = new VoteDao(connection);

        long voteId = voteDao.insert(vote);
        Assert.assertTrue(voteId > 0);

        List<Vote> readVotes = voteDao.readByBill(bill);

        Assert.assertEquals(1, readVotes.size());
    }


}
