package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.illinois.BillVotes;
import org.center4racialjustice.legup.illinois.Parser;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoadBill implements Handler {

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException {
        String billUrl = request.getParameter("url");
        String contents = Parser.readFileFromUrl(billUrl);
        BillVotes votes = Parser.parseFileContents(contents);

        VelocityContext vc = new VelocityContext();
        vc.put("yeas", votes.getYeas());
        vc.put("nays", votes.getNays());
        vc.put("presents", votes.getPresents());
        vc.put("notVotings", votes.getNotVotings());

        return vc;
    }

    @Override
    public String getTemplate() {
        return "bill_votes_page.vtl";
    }
}
