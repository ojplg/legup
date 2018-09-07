package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.db.hrorm.Dao;
import org.center4racialjustice.legup.domain.ReportCard;

import java.sql.Connection;
import java.util.List;

public class ReportCardDao {
    private final Dao<ReportCard> innerDao;

    public ReportCardDao(ConnectionWrapper wrapper){
        this(wrapper.getConnection());
    }

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

    public List<ReportCard> readAll(){
        return innerDao.selectAll();
    }

    public ReportCard read(long id){
        return innerDao.select(id);
    }
}
