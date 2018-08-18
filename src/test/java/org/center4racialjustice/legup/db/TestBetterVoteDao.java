package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.BetterVote;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.VoteSide;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestBetterVoteDao {

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

        Statement statement = connection.createStatement();
        String insertSql =
                "insert into votes (bill_id, legislator_id, vote_side) values "
                + "(" + billId + ", " + wilsonId + ", " + "'N' ) RETURNING ID";
        ResultSet resultSet = statement.executeQuery(insertSql);
        resultSet.next();
        long voteId = resultSet.getLong("id");

        BetterVoteDao voteDao = new BetterVoteDao(connection);

        BetterVote vote = voteDao.readOne(voteId);
        Assert.assertEquals("Wilson", vote.getLegislator().getFirstName());
        Assert.assertEquals(123, vote.getBill().getNumber());
        Assert.assertEquals(VoteSide.Nay, vote.getVoteSide());
    }

}
