package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.DaoBuilders;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Committee;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;
import org.hrorm.Dao;
import org.hrorm.Pair;

import java.util.List;

import static org.hrorm.Where.where;

public class ViewMain implements Responder {

    private final ConnectionPool connectionPool;

    public ViewMain(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        return connectionPool.useConnection(connection ->  {
            BillDao billDao = new BillDao(connection);
            List<Long> billSessions = billDao.uniqueSessions();

            LegislatorDao legislatorDao = new LegislatorDao(connection);
            List<Long> legislatorSessions = legislatorDao.distinctSessions();

            Dao<Committee> committeeDao = DaoBuilders.COMMITTEE.buildDao(connection);
            List<Pair<Long, Chamber>> committees =  committeeDao.selectDistinct("session_number", "chamber", where());

            HtmlLegupResponse response = HtmlLegupResponse.withHelp(this.getClass(), submission.getLoggedInUser());
            response.putVelocityData("billSessions", billSessions);
            response.putVelocityData("legislatorSessions", legislatorSessions);
            response.putVelocityData("committees", committees);
            return response;
        });
    }

}
