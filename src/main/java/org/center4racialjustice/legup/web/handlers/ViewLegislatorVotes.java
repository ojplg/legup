package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.db.VoteDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ViewLegislatorVotes implements Handler {

    private final ConnectionPool connectionPool;

    public ViewLegislatorVotes(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException, SQLException {

        String legislatorIdParameter = request.getParameter("legislator_id");
        long legislatorId = Long.parseLong(legislatorIdParameter);

        Connection connection = null;

        try {
            connection = connectionPool.getConnection();
            LegislatorDao legislatorDao = new LegislatorDao(connection);

            Legislator legislator = legislatorDao.read(legislatorId);

            VoteDao voteDao = new VoteDao(connection);

            List<Vote> votes = voteDao.readByLegislator(legislator);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("votes", votes);
            velocityContext.put("legislator", legislator);

            return velocityContext;
        } finally {
            if (connection !=null){
                connection.close();
            }
        }

    }
}