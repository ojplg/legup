package org.center4racialjustice.legup.db;

import org.hrorm.Dao;
import org.center4racialjustice.legup.domain.ReportCard;

import java.sql.Connection;
import java.util.List;

public class ReportCardDao {
    private final Dao<ReportCard> innerDao;

    public ReportCardDao(Connection connection) {
        this.innerDao = DaoBuilders.REPORT_CARDS.buildDao(connection);
    }

    public long save(ReportCard reportCard){
        if( reportCard.getId() == null ) {
            return innerDao.insert(reportCard);
        } else {
            innerDao.update(reportCard);
            return reportCard.getId();
        }
    }

    public ReportCard selectByName(String name){
        ReportCard reportCard = new ReportCard();
        reportCard.setName(name);
        return innerDao.selectOne(reportCard, "NAME");
    }

    public List<ReportCard> readAll(){
        return innerDao.select();
    }

    public ReportCard read(long id){
        return innerDao.selectOne(id);
    }
}
