package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Arrays;
import java.util.List;

public class ViewNewReportCardForm implements Responder {
    @Override
    public LegupResponse handle(LegupSubmission submission) {
        return HtmlLegupResponse.withLinks(ViewNewReportCardForm.class, submission.getLoggedInUser(), navLinks());
    }

    private List<NavLink> navLinks(){
        return Arrays.asList(
                new NavLink("Report Card Index", "/legup/view_report_cards")
        );
    }

}
