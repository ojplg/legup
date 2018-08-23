package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.illinois.BillSearcher;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.Util;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ViewBillSearchResults implements Handler {

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException, SQLException {

        String chamberString = request.getParameter("chamber");
        Chamber chamber = Chamber.fromString(chamberString);
        Long number = Util.getLongParameter(request, "number");

        BillSearcher searcher = new BillSearcher();

        String billHomePageUrl = searcher.searchForBaseUrl(chamber, number);
        String votesUrl = searcher.convertToVotesPage(billHomePageUrl);

        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("chamber", chamberString);
        velocityContext.put("number", number);

        velocityContext.put("bill_home_page_url", billHomePageUrl);
        velocityContext.put("bill_vote_page_url", votesUrl);

        return velocityContext;
    }
}
