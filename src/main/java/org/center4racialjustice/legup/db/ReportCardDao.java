package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportFactor;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

public class ReportCardDao {
    private final ReportFactorDao reportFactorDao;
    private final org.center4racialjustice.legup.db.hrorm.Dao<ReportCard> innerDao;

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
        List<ReportCard> reportCards = readCards( Collections.singletonList(id));
        return org.center4racialjustice.legup.db.hrorm.DaoHelper.fromSingletonList(reportCards, "Reading report card for " + id);
    }

    private List<ReportCard> readCards(List<Long> ids){
        List<ReportCard> reportCards = innerDao.selectMany(ids);
        for(ReportCard reportCard : reportCards){
            List<ReportFactor> factors = reportFactorDao.readByReportCardId(reportCard.getId());
            reportCard.setReportFactors(factors);
        }
        return reportCards;
    }

}
