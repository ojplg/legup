package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportFactor;
import org.center4racialjustice.legup.domain.VoteSide;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

public class TestReportCardDao {

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
        statement.execute("delete from bills");
        statement.execute("delete from report_factors");
        statement.execute("delete from report_cards");
        statement.close();
        connection.close();
    }

    @Test
    public void testSaveAndReadAll(){

        ReportCard reportCard = new ReportCard();
        reportCard.setName("Simple Card");
        reportCard.setSessionNumber(2018);

        Connection connection = connect();
        ReportCardDao reportCardDao = new ReportCardDao(connection);

        long reportCardId = reportCardDao.save(reportCard);

        Assert.assertTrue(reportCardId > 0);

        List<ReportCard> readCards = reportCardDao.readAll();

        Assert.assertEquals(1, readCards.size());
    }

    @Test
    public void testSaveWithFactor(){

        Connection connection = connect();

        Bill bill = new Bill();
        bill.setSession(123);
        bill.setNumber(6);
        bill.setChamber(Chamber.House);

        BillDao billDao = new BillDao(connection);

        billDao.save(bill);

        ReportCard reportCard = new ReportCard();
        reportCard.setName("Card With Factor");
        reportCard.setSessionNumber(123);

        ReportFactor reportFactor = new ReportFactor();
        reportFactor.setBill(bill);
        reportFactor.setVoteSide(VoteSide.Nay);

        reportCard.setReportFactors(Collections.singletonList(reportFactor));

        ReportCardDao reportCardDao = new ReportCardDao(connection);

        reportCardDao.save(reportCard);
        long reportCardId = reportCard.getId();

        ReportCard readCard = reportCardDao.read(reportCardId);
        Assert.assertEquals("Card With Factor", readCard.getName());
        Assert.assertEquals(1, readCard.getReportFactors().size());
        Assert.assertEquals(VoteSide.Nay, readCard.getReportFactors().get(0).getVoteSide());

    }

}
