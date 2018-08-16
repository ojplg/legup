package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public interface Handler {
    VelocityContext handle(Request request, HttpServletResponse httpServletResponse)
            throws IOException, SQLException;
    String getTemplate();
}