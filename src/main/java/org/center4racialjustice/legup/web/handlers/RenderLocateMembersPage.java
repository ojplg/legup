package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;

public class RenderLocateMembersPage implements Handler {

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) {
        return new VelocityContext();
    }

    @Override
    public String getTemplate() {
        return "locate_members_form.vtl";
    }
}