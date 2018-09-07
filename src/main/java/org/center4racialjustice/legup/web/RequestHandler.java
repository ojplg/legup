package org.center4racialjustice.legup.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.Writer;

public class RequestHandler {

    private static final Logger log = LogManager.getLogger(RequestHandler.class);

    private final Handler handler;
    private final String convertedName;

    public RequestHandler(Handler handler) {
        this.handler = handler;
        this.convertedName = convertToLowercaseWithUnderlines(handler.getClass().getSimpleName());
    }

    public void processRequest(Request request, HttpServletResponse httpServletResponse) {
        try {
            log.info("Processing request with " + handler.getClass().getName());
            String templatePath = "/templates/" + getTemplateName();
            Writer writer = httpServletResponse.getWriter();
            VelocityContext velocityContext = handler.handle(request, httpServletResponse);
            velocityContext.put("contents", templatePath);
            Template template = Velocity.getTemplate("/templates/container.vtl");
            template.merge(velocityContext, writer);
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