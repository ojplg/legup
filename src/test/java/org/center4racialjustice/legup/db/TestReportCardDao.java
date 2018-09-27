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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

public class TestReportCardDao {

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
        statement.execute("delete from report_cards");
        statement.close();
        connection.close();
    }

    @Test
    public void testReadAndWriteReportFactor(){
        Connection connection = DbTestConfigs.connect();

        Bill bill = new Bill();
        bill.setSession(123);
        bill.setNumber(6);
        bill.setChamber(Chamber.House);
        bill.setShortDescription("BILLBILL");

        BillDao billDao = new BillDao(connection);
        billDao.save(bill);

        ReportCard reportCard = new ReportCard();
        reportCard.setName("Foo");
        reportCard.setSessionNumber(12);

        ReportCardDao reportCardDao = new ReportCardDao(connection);
        reportCardDao.save(reportCard);

        Assert.assertNotNull(reportCard.getId());

        ReportFactor reportFactor = new ReportFactor();
        reportFactor.setBill(bill);
        reportFactor.setVoteSide(VoteSide.Nay);
        reportFactor.setReportCardId(reportCard.getId());

        ReportFactorDao reportFactorDao = new ReportFactorDao(connection);

        reportFactorDao.save(reportFactor);

        Assert.assertNotNull(reportFactor.getId());

        ReportFactor readFactor = reportFactorDao.readByReportCardId(reportCard.getId()).get(0);

        Assert.assertEquals("BILLBILL", readFactor.getBill().getShortDescription());

    }


    @Test
    public void testSaveAndReadAll(){

        ReportCard reportCard = new ReportCard();
        reportCard.setName("Simple Card");
        reportCard.setSessionNumber(2018);

        Connection connection = DbTestConfigs.connect();
        ReportCardDao reportCardDao = new ReportCardDao(connection);

        long reportCardId = reportCardDao.save(reportCard);

        Assert.assertTrue(reportCardId > 0);

        List<ReportCard> readCards = reportCardDao.readAll();

        Assert.assertEquals(1, readCards.size());
    }

    @Test
    public void testSaveWithFactor(){

        Connection connection = DbTestConfigs.connect();

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

    @Test
    public void testSaveWithFactorUpdatesNewFactorAdded(){

        Connection connection = DbTestConfigs.connect();

        Bill bill1 = new Bill();
        bill1.setSession(123);
        bill1.setNumber(1);
        bill1.setChamber(Chamber.House);

        Bill bill2 = new Bill();
        bill2.setSession(123);
        bill2.setNumber(2);
        bill2.setChamber(Chamber.House);

        BillDao billDao = new BillDao(connection);

        billDao.save(bill1);
        billDao.save(bill2);

        ReportCard reportCard = new ReportCard();
        reportCard.setName("Card With Factor");
        reportCard.setSessionNumber(123);

        ReportFactor reportFactor = new ReportFactor();
        reportFactor.setBill(bill1);
        reportFactor.setVoteSide(VoteSide.Nay);

        reportCard.setReportFactors(Collections.singletonList(reportFactor));

        ReportCardDao reportCardDao = new ReportCardDao(connection);

        reportCardDao.save(reportCard);
        long reportCardId = reportCard.getId();

        ReportCard readCard = reportCardDao.read(reportCardId);

        ReportFactor factor2 = new ReportFactor();
        factor2.setBill(bill2);
        factor2.setVoteSide(VoteSide.Yea);

        readCard.addReportFactor(factor2);
        readCard.setName("Card With Factor Updated");

        reportCardDao.save(readCard);

        ReportCard secondCardRead = reportCardDao.read(reportCardId);
        Assert.assertEquals("Card With Factor Updated", secondCardRead.getName());
        Assert.assertEquals(2, secondCardRead.getReportFactors().size());
        Assert.assertEquals(VoteSide.Nay, secondCardRead.getReportFactors().get(0).getVoteSide());
    }

    @Test
    public void testSaveWithFactorUpdateVoteSide(){

        Connection connection = DbTestConfigs.connect();

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

        readCard.getReportFactors().get(0).setVoteSide(VoteSide.Yea);
        reportCardDao.save(readCard);

        ReportCard secondReadCard = reportCardDao.read(reportCardId);

        Assert.assertEquals("Card With Factor", secondReadCard.getName());
        Assert.assertEquals(1, secondReadCard.getReportFactors().size());
        Assert.assertEquals(VoteSide.Yea, secondReadCard.getReportFactors().get(0).getVoteSide());

        readCard.getReportFactors().get(0).setVoteSide(VoteSide.Yea);

    }

    @Test
    public void testSelectByName(){
        Connection connection = DbTestConfigs.connect();

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

        ReportCard readCard = reportCardDao.selectByName("Card With Factor");
        Assert.assertEquals("Card With Factor", readCard.getName());
        Assert.assertEquals(1, readCard.getReportFactors().size());
        Assert.assertEquals(VoteSide.Nay, readCard.getReportFactors().get(0).getVoteSide());
        Assert.assertEquals(bill, readCard.getReportFactors().get(0).getBill());

    }

}
