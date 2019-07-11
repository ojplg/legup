package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Chamber;
import org.hrorm.Dao;
import org.center4racialjustice.legup.domain.Legislator;
import org.hrorm.Where;

import java.sql.Connection;
import java.util.List;

import static org.hrorm.Where.where;;
import static org.hrorm.Operator.EQUALS;


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
        return innerDao.selectOne(id);
    }

    public List<Legislator> readAll(){
        return innerDao.select();
    }

    public List<Legislator> readBySession(long session){
        Legislator legislator = new Legislator();
        legislator.setSessionNumber(session);
        return innerDao.select(legislator, "SESSION_NUMBER");
    }

    public List<Legislator> readByChamberAndSession(Chamber chamber, Long session){
        return innerDao.select(
                where("SESSION_NUMBER", EQUALS, session)
                .and("CHAMBER", EQUALS, chamber.getName()));
    }

    public Legislator readByMemberId(String memberId){
        Legislator legislator = new Legislator();
        legislator.setMemberId(memberId);
        return innerDao.selectOne(legislator,"MEMBER_ID");
    }

    public List<Long> distinctSessions(){
        return innerDao.selectDistinct("session_number", new Where());
    }
}
