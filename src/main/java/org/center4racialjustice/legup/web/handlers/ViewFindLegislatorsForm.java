package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;

public class ViewFindLegislatorsForm implements Handler {

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) {
        return new VelocityContext();
    }

    @Override
    public String getTemplate() {
        return "view_find_legislators_form.vtl";
    }
}