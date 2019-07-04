package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.User;
import org.center4racialjustice.legup.service.UserService;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.SecuredResponder;

import java.util.List;

public class ViewOrganizations implements SecuredResponder {

    private final UserService userService;

    public ViewOrganizations(ConnectionPool connectionPool){
        this.userService = new UserService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        List<Organization> orgs = userService.allOrganizations();

        HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());

        response.putVelocityData("organizations", orgs);
        return response;
    }

    @Override
    public boolean permitted(LegupSubmission submission){
        return submission.isSuperUserRequest();
    }

}
