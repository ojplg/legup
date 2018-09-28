package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

public class ViewBillSearchForm implements Responder {

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        return new LegupResponse(this.getClass());
    }
}
