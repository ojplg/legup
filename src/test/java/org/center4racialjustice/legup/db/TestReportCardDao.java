package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportFactor;
import org.center4racialjustice.legup.domain.VoteSide;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

public class TestReportCardDao {

    static {
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }

    @BeforeClass
    @AfterClass
    public static void setUp() throws SQLException {
        clearTables();
    }

    private static void clearTables() throws SQLException {
        Connection connection = DbTestConfigs.connect();
        Statement statement = connection.createStatement();
        statement.execute("delete from bill_actions");
        statement.execute("delete from bill_action_loads");
        statement.execute("delete from report_factors");
        statement.execute("delete from report_card_legislators");
        statement.execute("delete from bills");
        statement.execute("delete from report_cards");
        connection.commit();
        statement.close();
        connection.close();
    }

    @Test
    public void testSaveCardThenAddFactor() throws SQLException {

        long id;
        Bill bill;
        {
            Connection connection = DbTestConfigs.connect();

            bill = new Bill();
            bill.setSession(123);
            bill.setNumber(6);
            bill.setChamber(Chamber.House);
            bill.setShortDescription("BILLBILL");

            BillDao billDao = new BillDao(connection);
            billDao.insert(bill);

            connection.commit();
        }
        {
            Connection connection = DbTestConfigs.connect();
            ReportCardDao reportCardDao = new ReportCardDao(connection);
            ReportCard reportCard = new ReportCard();
            reportCard.setName("Foo");
            reportCard.setSessionNumber(123);

            id = reportCardDao.save(reportCard);

            connection.commit();
        }
        {
            Connection connection = DbTestConfigs.connect();
            ReportCardDao reportCardDao = new ReportCardDao(connection);
            ReportCard reportCard = reportCardDao.read(id);

            ReportFactor reportFactor = new ReportFactor();
            reportFactor.setBill(bill);
            reportFactor.setVoteSide(VoteSide.Nay);

            reportCard.addReportFactor(reportFactor);

            reportCardDao.save(reportCard);

            connection.commit();
        }

        {
            Connection connection = DbTestConfigs.connect();
            ReportCardDao reportCardDao = new ReportCardDao(connection);

            ReportCard reportCard = reportCardDao.read(id);

            Assert.assertEquals(1, reportCard.getReportFactors().size());
            Assert.assertEquals("BILLBILL", reportCard.getReportFactors().get(0).getBill().getShortDescription());
        }
    }


    @Test
    public void testSaveAndReadAll() throws SQLException {

        clearTables();

        ReportCard reportCard = new ReportCard();
        reportCard.setName("Simple Card");
        reportCard.setSessionNumber(2018);

        Connection connection = DbTestConfigs.connect();
        ReportCardDao reportCardDao = new ReportCardDao(connection);

        long reportCardId = reportCardDao.save(reportCard);

        connection.commit();

        Assert.assertTrue(reportCardId > 0);

        List<ReportCard> readCards = reportCardDao.readAll();

        Assert.assertEquals(1, readCards.size());
    }

    @Test
    public void testSaveWithFactor() throws SQLException {

        long reportCardId;
        {
            Connection connection = DbTestConfigs.connect();

            Bill bill = new Bill();
            bill.setSession(123);
            bill.setNumber(61);
            bill.setChamber(Chamber.House);

            BillDao billDao = new BillDao(connection);

            billDao.insert(bill);

            ReportCard reportCard = new ReportCard();
            reportCard.setName("Card With Factor");
            reportCard.setSessionNumber(123);

            ReportFactor reportFactor = new ReportFactor();
            reportFactor.setBill(bill);
            reportFactor.setVoteSide(VoteSide.Nay);

            reportCard.setReportFactors(Collections.singletonList(reportFactor));

            ReportCardDao reportCardDao = new ReportCardDao(connection);

            reportCardId = reportCardDao.save(reportCard);

            connection.commit();
        }

        {
            Connection connection = DbTestConfigs.connect();
            ReportCardDao reportCardDao = new ReportCardDao(connection);

            ReportCard readCard = reportCardDao.read(reportCardId);
            Assert.assertEquals("Card With Factor", readCard.getName());
            Assert.assertEquals(1, readCard.getReportFactors().size());
            Assert.assertEquals(VoteSide.Nay, readCard.getReportFactors().get(0).getVoteSide());
        }
    }

    @Test
    public void testSaveWithFactorUpdatesNewFactorAdded() throws SQLException {

        long reportCardId;
        Bill bill1;
        Bill bill2;
        {
            bill1 = new Bill();
            bill1.setSession(123);
            bill1.setNumber(1);
            bill1.setChamber(Chamber.House);

            bill2 = new Bill();
            bill2.setSession(123);
            bill2.setNumber(2);
            bill2.setChamber(Chamber.House);


            Connection connection = DbTestConfigs.connect();
            BillDao billDao = new BillDao(connection);

            billDao.insert(bill1);
            billDao.insert(bill2);

            connection.commit();
        }
        {
            Connection connection = DbTestConfigs.connect();

            ReportCard reportCard = new ReportCard();
            reportCard.setName("Card With Factor Again");
            reportCard.setSessionNumber(123);

            ReportFactor reportFactor = new ReportFactor();
            reportFactor.setBill(bill1);
            reportFactor.setVoteSide(VoteSide.Nay);

            reportCard.addReportFactor(reportFactor);

            ReportCardDao reportCardDao = new ReportCardDao(connection);

            reportCardDao.save(reportCard);
            reportCardId = reportCard.getId();
            connection.commit();
        }
        {
            Connection connection = DbTestConfigs.connect();

            ReportCardDao reportCardDao = new ReportCardDao(connection);

            ReportCard readCard = reportCardDao.read(reportCardId);

            Assert.assertEquals(1, readCard.getReportFactors().size());

            ReportFactor factor2 = new ReportFactor();

            factor2.setBill(bill2);
            factor2.setVoteSide(VoteSide.Yea);

            readCard.addReportFactor(factor2);
            readCard.setName("Card With Factor Updated");

            Assert.assertEquals(2, readCard.getReportFactors().size());

            reportCardDao.save(readCard);

            connection.commit();
        }
        {
            Connection connection = DbTestConfigs.connect();
            ReportCardDao reportCardDao = new ReportCardDao(connection);

            ReportCard secondCardRead = reportCardDao.read(reportCardId);
            Assert.assertEquals("Card With Factor Updated", secondCardRead.getName());
            Assert.assertEquals(2, secondCardRead.getReportFactors().size());
            Assert.assertEquals(VoteSide.Nay, secondCardRead.getReportFactors().get(0).getVoteSide());
        }
    }

    @Test
    public void testSaveWithFactorUpdateVoteSide() throws SQLException {

        Connection connection = DbTestConfigs.connect();

        Bill bill = new Bill();
        bill.setSession(123);
        bill.setNumber(62);
        bill.setChamber(Chamber.House);

        BillDao billDao = new BillDao(connection);

        billDao.insert(bill);

        ReportCard reportCard = new ReportCard();
        reportCard.setName("Card With Factor 3");
        reportCard.setSessionNumber(123);

        ReportFactor reportFactor = new ReportFactor();
        reportFactor.setBill(bill);
        reportFactor.setVoteSide(VoteSide.Nay);

        reportCard.setReportFactors(Collections.singletonList(reportFactor));

        ReportCardDao reportCardDao = new ReportCardDao(connection);

        reportCardDao.save(reportCard);
        long reportCardId = reportCard.getId();

        ReportCard readCard = reportCardDao.read(reportCardId);
        Assert.assertEquals("Card With Factor 3", readCard.getName());
        Assert.assertEquals(1, readCard.getReportFactors().size());
        Assert.assertEquals(VoteSide.Nay, readCard.getReportFactors().get(0).getVoteSide());

        readCard.getReportFactors().get(0).setVoteSide(VoteSide.Yea);
        reportCardDao.save(readCard);

        connection.commit();

        ReportCard secondReadCard = reportCardDao.read(reportCardId);

        Assert.assertEquals("Card With Factor 3", secondReadCard.getName());
        Assert.assertEquals(1, secondReadCard.getReportFactors().size());
        Assert.assertEquals(VoteSide.Yea, secondReadCard.getReportFactors().get(0).getVoteSide());

        readCard.getReportFactors().get(0).setVoteSide(VoteSide.Yea);

    }

    @Test
    public void testSelectByName() throws SQLException {
        Connection connection = DbTestConfigs.connect();

        Bill bill = new Bill();
        bill.setSession(123);
        bill.setNumber(63);
        bill.setChamber(Chamber.House);

        BillDao billDao = new BillDao(connection);

        billDao.insert(bill);

        ReportCard reportCard = new ReportCard();
        reportCard.setName("Card With Factor 4");
        reportCard.setSessionNumber(123);

        ReportFactor reportFactor = new ReportFactor();
        reportFactor.setBill(bill);
        reportFactor.setVoteSide(VoteSide.Nay);

        reportCard.setReportFactors(Collections.singletonList(reportFactor));

        ReportCardDao reportCardDao = new ReportCardDao(connection);

        reportCardDao.save(reportCard);

        connection.commit();

        ReportCard readCard = reportCardDao.selectByName("Card With Factor 4");
        Assert.assertEquals("Card With Factor 4", readCard.getName());
        Assert.assertEquals(1, readCard.getReportFactors().size());
        Assert.assertEquals(VoteSide.Nay, readCard.getReportFactors().get(0).getVoteSide());
        Assert.assertEquals(bill, readCard.getReportFactors().get(0).getBill());

    }

}
