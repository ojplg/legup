package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.User;
import org.center4racialjustice.legup.service.UserService;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.List;

public class SaveNewUser implements Responder {

    private final UserService userService;

    public SaveNewUser(ConnectionPool connectionPool){
        this.userService = new UserService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        String organizationName = submission.getParameter("new_user_organization");
        String email = submission.getParameter("new_user_email");
        String password = submission.getParameter("new_user_password");

        User user = userService.insertNewUserAndOrganization(organizationName, email, password);

        List<Organization> organizations = userService.findOrganizationsOfUser(user);
        submission.setLoggedInUser(user, organizations);

        HtmlLegupResponse legupResponse = HtmlLegupResponse.simpleResponse(SaveNewUser.class, submission.getLoggedInUser());
        legupResponse.putVelocityData("user", user);
        return legupResponse;
    }
}
