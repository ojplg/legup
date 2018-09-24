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
        // FIXME: this is pretty hacky. Need to decide on one framework to rule them all.
        if ( handler instanceof  ResponderHandler ){
            ResponderHandler responderHandler = (ResponderHandler) handler;
            this.convertedName = convertToLowercaseWithUnderlines(responderHandler.getRouteName());
        } else {
            this.convertedName = convertToLowercaseWithUnderlines(handler.getClass().getSimpleName());
        }
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
        StringBuilder buf = new StringBuilder();
        for (int idx = 0; idx < name.length(); idx++) {
            char c = name.charAt(idx);
            if (idx == 0) {
                buf.append(Character.toLowerCase(c));
            } else if(Character.isUpperCase(c)) {
                buf.append('_');
                buf.append(Character.toLowerCase(c));
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}