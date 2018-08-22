package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.ReportCard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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

}
