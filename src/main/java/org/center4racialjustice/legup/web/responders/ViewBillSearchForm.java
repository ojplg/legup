package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;
import org.center4racialjustice.legup.web.Util;

import java.util.Collections;
import java.util.List;

public class ViewBillSearchForm implements Responder {

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        return new HtmlLegupResponse(this.getClass());
    }


    @Override
    public List<NavLink> navLinks() {
        return Collections.singletonList(
                new NavLink("Bills Index", "/legup/view_bills")
        );
    }

    @Override
    public String helpLink() {
        return "/legup/help/" + Util.classNameToLowercaseWithUnderlines(ViewBillSearchForm.class);
    }

}
