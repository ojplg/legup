package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.User;
import org.center4racialjustice.legup.service.UserService;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

public class SaveNewUser implements Responder {

    private final UserService userService;

    public SaveNewUser(ConnectionPool connectionPool){
        this.userService = new UserService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        String organizationName = submission.getParameter("new_user_organization");
        Organization organization = userService.insertNewOrganization(organizationName);

        String email = submission.getParameter("new_user_email");
        String password = submission.getParameter("new_user_password");

        User user = userService.insertNewUser(email, password, organization);

        HtmlLegupResponse legupResponse = HtmlLegupResponse.simpleResponse(SaveNewUser.class, submission.getLoggedInUser());
        legupResponse.putVelocityData("user", user);
        return legupResponse;
    }
}
