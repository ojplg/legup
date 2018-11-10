package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

public class ViewBillForm implements Responder {

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        return HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());
    }
}
