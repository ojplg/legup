package org.center4racialjustice.legup.db;

import org.hrorm.Dao;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;

import java.sql.Connection;
import java.util.List;

public class LegislatorDao {

    private final Dao<Legislator> innerDao;

    public LegislatorDao(ConnectionWrapper wrapper){
        this(wrapper.getConnection());
    }

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

    public List<Legislator> readByChamberDistrictSession(Chamber chamber, long district, long session){
        // NOTE: These are not unique! Legislators can be replaced mid-term.
        Legislator legislator = new Legislator();
        legislator.setChamber(chamber);
        legislator.setDistrict(district);
        legislator.setSessionNumber(session);
        return innerDao.selectManyByColumns(legislator, "CHAMBER", "DISTRICT", "SESSION_NUMBER");
    }
}
