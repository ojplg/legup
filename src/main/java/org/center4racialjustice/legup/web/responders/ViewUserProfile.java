package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.SecuredResponder;

public class ViewUserProfile implements SecuredResponder {

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());

        response.putVelocityData("user", submission.getLoggedInUser());
        response.putVelocityData("organization", submission.getLoggedInUser().getOrganization());

        return response;
    }

    @Override
    public boolean permitted(LegupSubmission submission){
        return submission.getLoggedInUser() != null;
    }

}
