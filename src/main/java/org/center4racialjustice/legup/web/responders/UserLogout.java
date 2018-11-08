package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.web.ContinueLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

public class UserLogout implements Responder {

    private static final Logger log = LogManager.getLogger(UserLogin.class);

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        log.info("Logging out: " + submission.getLoggedInUser());

        submission.logout();

        // TODO: this should go to the login page
        return new ContinueLegupResponse(ViewReportCards.class);
    }

}
