package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;

public class ViewBillSearchForm implements Handler {

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {
        return new VelocityContext();
    }
}
