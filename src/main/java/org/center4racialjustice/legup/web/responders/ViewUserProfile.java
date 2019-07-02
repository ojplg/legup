package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.service.UserService;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.SecuredResponder;

public class ViewUserProfile implements SecuredResponder {

    private final UserService userService;

    public ViewUserProfile(ConnectionPool connectionPool){
        this.userService = new UserService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());

        response.putVelocityData("user", submission.getLoggedInUser());
        response.putVelocityData("organizations", userService.findOrganizationsOfUser(submission.getLoggedInUser()));

        return response;
    }

    @Override
    public boolean permitted(LegupSubmission submission){
        return submission.getLoggedInUser() != null;
    }

}
