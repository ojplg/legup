package org.center4racialjustice.legup.db;

import org.hrorm.Dao;
import org.center4racialjustice.legup.domain.Legislator;

import java.sql.Connection;
import java.util.List;

public class LegislatorDao {

    private final Dao<Legislator> innerDao;

    public LegislatorDao(Connection connection){
        this.innerDao = DaoBuilders.LEGISLATORS.buildDao(connection);
    }

    public long insert(Legislator legislator){
        return innerDao.insert(legislator);
    }

    public void update(Legislator legislator){
        innerDao.update(legislator);
    }

    public Legislator read(long id){
        return innerDao.select(id);
    }

    public List<Legislator> readAll(){
        return innerDao.selectAll();
    }

    public List<Legislator> readBySession(long session){
        Legislator legislator = new Legislator();
        legislator.setSessionNumber(session);
        return innerDao.selectManyByColumns(legislator, "SESSION_NUMBER");
    }

    public Legislator readByMemberId(String memberId){
        Legislator legislator = new Legislator();
        legislator.setMemberId(memberId);
        return innerDao.selectByColumns(legislator,"MEMBER_ID");
    }
}
