package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.User;
import org.center4racialjustice.legup.service.UserService;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.SecuredResponder;

import java.util.List;

public class ViewUsers implements SecuredResponder {

    private final UserService userService;

    public ViewUsers(ConnectionPool connectionPool) {
        this.userService = new UserService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        List<User> allUsers = userService.allUsers();

        HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(ViewUsers.class, submission.getLoggedInUser());
        response.putVelocityData("users", allUsers);
        return response;
    }

    @Override
    public boolean permitted(LegupSubmission legupSubmission) {
        return legupSubmission.isSuperUserRequest();
    }
}
