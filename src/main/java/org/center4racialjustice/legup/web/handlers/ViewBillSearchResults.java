package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.Util;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ViewBillSearchResults implements Handler {

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException, SQLException {

        String chamberString = request.getParameter("chamber");
        Long number = Util.getLongParameter(request, "number");


        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("chamber", chamberString);
        velocityContext.put("number", number);

        return velocityContext;
    }
}
