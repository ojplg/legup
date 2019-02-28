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

public class ViewOrganization implements SecuredResponder {

    private final UserService userService;

    public ViewOrganization(ConnectionPool connectionPool){
        this.userService = new UserService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        Organization organization = submission.getLoggedInUser().getOrganization();
        List<User> users = userService.findUsersInOrganization(organization);

        HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());

        response.putVelocityData("organization", organization);
        response.putVelocityData("users", users);

        return response;
    }

    @Override
    public boolean permitted(LegupSubmission submission){
        Long orgId = submission.getLongRequestParameter("organization_id");
        Organization organization = submission.getLoggedInUser().getOrganization();
        return organization.getId() == orgId;
    }

}
