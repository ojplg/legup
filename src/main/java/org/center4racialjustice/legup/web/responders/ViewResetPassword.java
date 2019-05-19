package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.service.UserService;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

public class ViewResetPassword implements Responder {

    private static final Logger log = LogManager.getLogger(UserLogin.class);

    private final UserService userService;

    public ViewResetPassword(ConnectionPool connectionPool){
        this.userService = new UserService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());
        response.putVelocityData("user", submission.getLoggedInUser());
        return response;
    }

    @Override
    public boolean isSecured() {
        return true;
    }
}
