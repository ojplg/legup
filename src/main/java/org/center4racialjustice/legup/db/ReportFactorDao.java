package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.db.hrorm.Dao;
import org.center4racialjustice.legup.domain.ReportFactor;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class ReportFactorDao {

    private final Dao<ReportFactor> innerDao;

    public ReportFactorDao(Connection connection) {
        this.innerDao = DaoBuilders.REPORT_FACTORS.buildDao(connection);
    }

    public List<ReportFactor> readByReportCardId(long reportCardId){
        ReportFactor reportFactor = new ReportFactor();
        reportFactor.setReportCardId(reportCardId);
        return innerDao.selectManyByColumns(reportFactor, Arrays.asList("REPORT_CARD_ID"));
    }

    public Long save(ReportFactor reportFactor){
        return innerDao.insert(reportFactor);
    }
}
