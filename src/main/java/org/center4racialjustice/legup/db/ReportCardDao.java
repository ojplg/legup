package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.db.hrorm.Dao;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportFactor;

import java.sql.Connection;
import java.util.List;

public class ReportCardDao {
    private final ReportFactorDao reportFactorDao;
    private final Dao<ReportCard> innerDao;

    public ReportCardDao(Connection connection) {
        this.innerDao = DaoBuilders.REPORT_CARDS.buildDao(connection);
        this.reportFactorDao = new ReportFactorDao(connection);
    }

    public long save(ReportCard reportCard){
        // simply delete all old factors for now. it's simple
        if( reportCard.getId() != null) {
            reportFactorDao.deleteByReportCardId(reportCard.getId());
        }
        if( reportCard.getId() == null ) {
            innerDao.insert(reportCard);
        } else {
            innerDao.update(reportCard);
        }
        for(ReportFactor factor : reportCard.getReportFactors()){
            factor.setReportCardId(reportCard.getId());
            reportFactorDao.save(factor);
        }
        return reportCard.getId();
    }

    public List<ReportCard> readAll(){
        return innerDao.selectAll();
    }

    public ReportCard read(long id){
        return innerDao.select(id);
    }
}
