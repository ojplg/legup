package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.illinois.LegislationType;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Collections;
import java.util.List;

public class ViewBillSearchForm implements Responder {

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        HtmlLegupResponse htmlLegupResponse =  HtmlLegupResponse.withHelp(this.getClass(), submission.getLoggedInUser());
        htmlLegupResponse.putVelocityData("sub_types", LegislationType.ALL_SUB_TYPES);
        htmlLegupResponse.putVelocityData("chambers", Chamber.ALL_CHAMBER_NAMES);
        return htmlLegupResponse;
    }

}
