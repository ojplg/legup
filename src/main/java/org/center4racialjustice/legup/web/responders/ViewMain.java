package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

public class ViewMain implements Responder {

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        HtmlLegupResponse response = HtmlLegupResponse.withHelp(this.getClass(), submission.getLoggedInUser());
        return response;
    }

}
