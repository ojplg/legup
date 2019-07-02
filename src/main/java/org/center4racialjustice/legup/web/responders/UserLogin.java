package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.User;
import org.center4racialjustice.legup.service.UserService;
import org.center4racialjustice.legup.web.ContinueLegupResponse;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.List;

public class UserLogin implements Responder {

    private static final Logger log = LogManager.getLogger(UserLogin.class);

    private final UserService userService;

    public UserLogin(ConnectionPool connectionPool){
        this.userService = new UserService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        String email = submission.getParameter("email");
        String password = submission.getParameter("password");

        log.info("User login attempt for " + email);

        User user = userService.login(email, password);

        if ( user == null ){
            log.info("User login failed");
            HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(ViewLogin.class, null);
            response.putVelocityData("loginFailed", Boolean.TRUE);
            return response;
        }

        log.info("User " + email + " is logged in");

        List<Organization> organizations = userService.findOrganizationsOfUser(user);
        submission.setLoggedInUser(user, organizations);


        return new ContinueLegupResponse(ViewUserProfile.class);
    }
}
