package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;

public interface Handler {
    VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse);
}
