package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillActionLoadDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.illinois.CollatedVote;
import org.center4racialjustice.legup.illinois.VotesLegislatorsCollator;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.SQLException;

public class SaveCollatedVotes implements Handler {

    private final ConnectionPool connectionPool;

    public SaveCollatedVotes(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse)
            throws SQLException {

        String key = request.getParameter("collated_votes_key");

        HttpSession session = request.getSession();
        String sessionKey = (String) session.getAttribute("collatedVotesKey");
        VotesLegislatorsCollator collator = (VotesLegislatorsCollator) session.getAttribute("collatedVotes");
        BillActionLoad billActionLoad = (BillActionLoad) session.getAttribute("billActionLoad");

        if ( ! key.equals(sessionKey) ){
            // this is an error condition
            // it should be reported to the user somehow
            throw new RuntimeException("Mismatched collation keys");
        }

        try (Connection connection = connectionPool.getConnection()) {

            BillDao billDao = connectionPool.getBillDao();
            Bill bill = billDao.findOrCreate(collator.getBillSession(), collator.getBillChamber(), collator.getBillNumber());

            billActionLoad.setBill(bill);
            BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
            long voteLoadId = billActionLoadDao.insert(billActionLoad);
            billActionLoad.setId(voteLoadId);

            BillActionDao billActionDao = new BillActionDao(connection);

            int savedCount = 0;
            for (CollatedVote collatedVote : collator.getAllCollatedVotes()) {
                Vote vote = collatedVote.asVote(bill, billActionLoad);
                BillAction billAction = BillAction.fromVote(vote);
                billActionDao.insert(billAction);
                savedCount++;
            }

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("saved_count", savedCount);
            velocityContext.put("chamber", bill.getChamber());
            velocityContext.put("bill_number", bill.getNumber());
            return velocityContext;

        }
    }
}
