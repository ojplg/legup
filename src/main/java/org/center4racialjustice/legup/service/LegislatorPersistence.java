package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Legislator;

import java.util.ArrayList;
import java.util.List;

public class LegislatorPersistence {

    private final ConnectionPool connectionPool;

    public LegislatorPersistence(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public int insertLegislators(List<Legislator> legislators){
        connectionPool.runAndCommit(connection ->
                {
                    LegislatorDao legislatorDao = new LegislatorDao(connection);
                    for(Legislator legislator : legislators) {
                        // NOTE: This should work by member id AND session
                        Legislator inDb = legislatorDao.readByMemberId(legislator.getMemberId());
                        if( inDb == null ){
                            legislatorDao.insert(legislator);
                        }
                        // Perhaps attempt an update?
                        // It's difficult. A legislator could have an update, even a name change.
                        // Or it could be a whole new person.
                        // This really needs to work. Safest is to ignore for now.
                    }

                });
        return legislators.size();
    }

    public List<Legislator> filterOutSavedLegislators(List<Legislator> legislators){
        List<Legislator> results = connectionPool.runAndCommit( connection ->
        {
            List<Legislator> unknownLegislators = new ArrayList<>();
            LegislatorDao legislatorDao = new LegislatorDao(connection);
            for(Legislator legislator : legislators) {
                // NOTE: This should work by member id AND session
                Legislator inDb = legislatorDao.readByMemberId(legislator.getMemberId());
                if( inDb == null ){
                    unknownLegislators.add(legislator);
                }
                // we could create a list of updates here .... See comment above
            }
            return unknownLegislators;
        });
        return results;
    }
}
