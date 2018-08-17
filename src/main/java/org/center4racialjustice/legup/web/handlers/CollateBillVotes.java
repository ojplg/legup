package org.center4racialjustice.legup.web.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.illinois.BillVotes;
import org.center4racialjustice.legup.illinois.VotesLegislatorsCollator;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CollateBillVotes implements Handler {

    private static final Logger log = LogManager.getLogger(CollateBillVotes.class);

    private final ConnectionPool connectionPool;

    public CollateBillVotes(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException, SQLException {

        HttpSession session = request.getSession();
        BillVotes billVotes = (BillVotes) session.getAttribute("billVotes");

        Connection connection = connectionPool.getConnection();
        LegislatorDao legislatorDao = new LegislatorDao(connection);
        List<Legislator> legislators = legislatorDao.readAll();
        connection.close();

        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("legislator_count" , legislators.size());
        velocityContext.put("vote_count", billVotes.totalVotes());

        VotesLegislatorsCollator collator = new VotesLegislatorsCollator(legislators, billVotes);
        collator.collate();

        velocityContext.put("collated_yeas", collator.getYeas());
        velocityContext.put("collated_nays", collator.getNays());
        velocityContext.put("collated_not_votings", collator.getNotVotings());
        velocityContext.put("collated_presents", collator.getPresents());
        velocityContext.put("uncollated", collator.getUncollated());

        return velocityContext;

    }

    @Override
    public String getTemplate() {
        return "collate_bill_votes.vtl";
    }
}
