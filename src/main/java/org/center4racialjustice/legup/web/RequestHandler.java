package org.center4racialjustice.legup.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public class RequestHandler {

    private static final Logger log = LogManager.getLogger(RequestHandler.class);

    private final Handler handler;
    private final String convertedName;

    public RequestHandler(Handler handler) {
        this.handler = handler;
        this.convertedName = convertToLowercaseWithUnderlines(handler.getClass().getSimpleName());
    }

    public void processRequest(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {
        try {
            log.info("Processing request with " + handler.getClass().getName());
            String templatePath = "/templates/" + getTemplateName();
            Writer writer = httpServletResponse.getWriter();
            VelocityContext velocityContext = handler.handle(request, legupSession, httpServletResponse);
            velocityContext.put("contents", templatePath);
            Velocity.mergeTemplate("/templates/container.vtl","ISO-8859-1",velocityContext, writer);
            request.setHandled(true);
        } catch (Exception ex) {
            log.error("Exception in request processing", ex);
            throw new RuntimeException(ex);
        }
    }

    public String getRouteName(){
        return "/" + convertedName;
    }

    public String getTemplateName(){
        return convertedName + ".vtl";
    }

    private String convertToLowercaseWithUnderlines(String name) {
        return Util.convertTitleCaseToLowercaseWithUnderlines(name);
    }
}