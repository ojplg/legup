package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.User;
import org.center4racialjustice.legup.service.UserService;
import org.center4racialjustice.legup.web.ContinueLegupResponse;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

public class SaveResetPassword implements Responder {

    private static final Logger log = LogManager.getLogger(SaveResetPassword.class);

    private final UserService userService;

    public SaveResetPassword(ConnectionPool connectionPool){
        this.userService = new UserService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        String oldPassword = submission.getParameter("old_password");
        String newPassword = submission.getParameter("new_password");
        User user = userService.login(submission.getLoggedInUser().getEmail(), oldPassword);

        if ( user == null ){
            log.info("Incorrect old password submitted");
            HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(ViewResetPassword.class, submission.getLoggedInUser());
            response.putVelocityData("user", submission.getLoggedInUser());
            response.putVelocityData("oldPasswordIncorrect", Boolean.TRUE);
            return response;
        }

        user.resetPassword(newPassword);

        userService.updateUser(user);

        submission.setLoggedInUser(user);

        return new ContinueLegupResponse(ViewUserProfile.class);
    }

    @Override
    public boolean isSecured() {
        return true;
    }
}
