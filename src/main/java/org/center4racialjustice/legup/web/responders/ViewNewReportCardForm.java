package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.service.UserService;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Arrays;
import java.util.List;

public class ViewNewReportCardForm implements Responder {

    private final UserService userService;

    public ViewNewReportCardForm(ConnectionPool connectionPool){
        this.userService = new UserService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        List<Organization> organizations = userService.findOrganizationsOfUser(submission.getLoggedInUser());
         HtmlLegupResponse response = HtmlLegupResponse.withLinks(ViewNewReportCardForm.class, submission.getLoggedInUser(), navLinks());
         response.putVelocityData("organizations", organizations);
         return response;
    }

    private List<NavLink> navLinks(){
        return Arrays.asList(
                new NavLink("Report Card Index", "/legup/view_report_cards")
        );
    }

}
