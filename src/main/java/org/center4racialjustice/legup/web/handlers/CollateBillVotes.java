package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.illinois.BillVotes;
import org.center4racialjustice.legup.illinois.VotesLegislatorsCollator;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

public class CollateBillVotes implements Handler {

    private final ConnectionPool connectionPool;

    public CollateBillVotes(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) {

        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {

            HttpSession session = request.getSession();
            BillVotes billVotes = (BillVotes) session.getAttribute("billVotes");
            session.removeAttribute("billVotes");

            LegislatorDao legislatorDao = new LegislatorDao(connection);
            List<Legislator> legislators = legislatorDao.readAll();
            connection.close();

            VelocityContext velocityContext = new VelocityContext();

            velocityContext.put("legislator_count", legislators.size());
            velocityContext.put("vote_count", billVotes.totalVotes());

            VotesLegislatorsCollator collator = new VotesLegislatorsCollator(legislators, billVotes);
            collator.collate();

            String collatedVotesKey = UUID.randomUUID().toString();

            session.setAttribute("collatedVotes", collator);
            session.setAttribute("collatedVotesKey", collatedVotesKey);

            velocityContext.put("collated_yeas", collator.getYeas());
            velocityContext.put("collated_nays", collator.getNays());
            velocityContext.put("collated_not_votings", collator.getNotVotings());
            velocityContext.put("collated_presents", collator.getPresents());
            velocityContext.put("uncollated", collator.getUncollated());
            velocityContext.put("collated_votes_key", collatedVotesKey);

            return velocityContext;
        }

    }
}
