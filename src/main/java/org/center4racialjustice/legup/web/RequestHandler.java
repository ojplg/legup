package org.center4racialjustice.legup.web;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public class RequestHandler {

    private final Handler handler;

    public RequestHandler(Handler handler){
        this.handler = handler;
    }

    public void processRequest(Request request, HttpServletResponse httpServletResponse)
    throws IOException  {
        System.out.println("Processing request with " + handler.getClass().getName());
        String templatePath = "/templates/" + handler.getTemplate();
        Writer writer = httpServletResponse.getWriter();
        VelocityContext velocityContext = handler.handle(request, httpServletResponse);
        velocityContext.put("contents",templatePath);
        Template template = Velocity.getTemplate("/templates/container.vtl");
        template.merge(velocityContext, writer);
        request.setHandled(true);
    }

}
