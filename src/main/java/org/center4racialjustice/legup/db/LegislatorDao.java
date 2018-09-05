package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.db.hrorm.Dao;
import org.center4racialjustice.legup.domain.Legislator;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class LegislatorDao {

    private final Dao<Legislator> innerDao;

    public LegislatorDao(Connection connection){
        this.innerDao = DaoBuilders.LEGISLATORS.buildDao(connection);
    }

    public long save(Legislator legislator){
        if( legislator.getId() == null ){
            return innerDao.insert(legislator);
        } else {
            innerDao.update(legislator);
            return legislator.getId();
        }
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
        return innerDao.selectManyByColumns(legislator, Arrays.asList("SESSION_NUMBER"));
    }
}
