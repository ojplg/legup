package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.VoteLoad;
import org.center4racialjustice.legup.illinois.BillVotes;
import org.center4racialjustice.legup.illinois.BillVotesParser;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

public class LoadBill implements Handler {

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException {
        String billUrl = request.getParameter("url");
        String contents = BillVotesParser.readFileFromUrl(billUrl);
        BillVotes votes = BillVotesParser.parseFileContents(contents);
        VoteLoad voteLoad = new VoteLoad();
        voteLoad.setUrl(billUrl);
        voteLoad.setCheckSum((long) contents.hashCode());
        voteLoad.setLoadTime(LocalDateTime.now());

        HttpSession session = request.getSession();
        // FIXME: Need a one-time ID here
        session.setAttribute("billVotes", votes);
        session.setAttribute("voteLoad", voteLoad);

        VelocityContext vc = new VelocityContext();
        vc.put("bill_chamber", votes.getBillChamber());
        vc.put("bill_number", votes.getBillNumber());
        vc.put("yeas", votes.getYeas());
        vc.put("nays", votes.getNays());
        vc.put("presents", votes.getPresents());
        vc.put("not_votings", votes.getNotVotings());

        return vc;
    }
}
