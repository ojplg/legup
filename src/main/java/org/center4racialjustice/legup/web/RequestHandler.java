package org.center4racialjustice.legup.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

public class RequestHandler {

    private static final Logger log = LogManager.getLogger(RequestHandler.class);

    private final Handler handler;

    public RequestHandler(Handler handler){
        this.handler = handler;
    }

    public void processRequest(Request request, HttpServletResponse httpServletResponse)
    throws IOException {
        try {
            log.info("Processing request with " + handler.getClass().getName());
            String templatePath = "/templates/" + handler.getTemplate();
            Writer writer = httpServletResponse.getWriter();
            VelocityContext velocityContext = handler.handle(request, httpServletResponse);
            velocityContext.put("contents", templatePath);
            Template template = Velocity.getTemplate("/templates/container.vtl");
            template.merge(velocityContext, writer);
            request.setHandled(true);
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

}
