package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.ReportFactor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class ReportFactorDao {

    private final org.center4racialjustice.legup.db.hrorm.Dao<ReportFactor> innerDao;
    private final Connection connection;

    public ReportFactorDao(Connection connection) {
        this.innerDao = DaoBuilders.REPORT_FACTORS.buildDao(connection);
        this.connection = connection;
    }

    public List<ReportFactor> readByReportCardId(long reportCardId){
        ReportFactor reportFactor = new ReportFactor();
        reportFactor.setReportCardId(reportCardId);
        return innerDao.selectManyByColumns(reportFactor, Arrays.asList("REPORT_CARD_ID"));
    }

    public void deleteByReportCardId(long reportCardId){
        try {
            Statement statement = connection.createStatement();
            statement.execute("delete from report_factors where report_card_id = " + reportCardId);
            statement.close();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    public Long save(ReportFactor reportFactor){
        return innerDao.insert(reportFactor);
    }
}
