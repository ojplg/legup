package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.illinois.VotesLegislatorsCollator;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class SaveCollatedVotes implements Handler {

    private final ConnectionPool connectionPool;

    public SaveCollatedVotes(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse)
            throws IOException, SQLException {

        String key = request.getParameter("collated_votes_key");

        HttpSession session = request.getSession();
        String sessionKey = (String) session.getAttribute("collatedVotesKey");
        VotesLegislatorsCollator collator = (VotesLegislatorsCollator) session.getAttribute("collatedVotes");

        if ( ! key.equals(sessionKey) ){
            // this is an error condition
            // it should be reported to the user somehow
            throw new RuntimeException("Mismatched collation keys");
        }

        Connection connection = null;
        try {
            connection = connectionPool.getConnection();

            BillDao billDao = new BillDao(connection);
            Bill bill = billDao.findOrCreate(collator.getBillChamber(), collator.getBillNumber());

//            VoteDao voteDao = new VoteDao(connection);
//
//            int savedCount = 0;
//            long billId = bill.getId();
//            for(CollatedVote collatedVote :  collator.getAllCollatedVotes()){
//                Vote vote = collatedVote.asVote(billId);
//                voteDao.save(vote);
//                savedCount++;
//            }

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("saved_count", 0);
            velocityContext.put("chamber", bill.getChamber());
            velocityContext.put("bill_number", bill.getNumber());
            return velocityContext;

        } finally {
            if( connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public String getTemplate() {
        return "save_collated_votes_results.vtl";
    }
}
