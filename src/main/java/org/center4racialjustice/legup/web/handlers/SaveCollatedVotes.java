package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.illinois.VotesLegislatorsCollator;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class SaveCollatedVotes implements Handler {

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

        // save the votes somehow. :)

        VelocityContext velocityContext = new VelocityContext();

        return velocityContext;

    }

    @Override
    public String getTemplate() {
        return "save_collated_votes_results.vtl";
    }
}
