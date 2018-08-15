package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

public class AppHandler extends AbstractHandler {

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                   HttpServletResponse httpServletResponse) throws IOException, ServletException {

        String appPath = request.getPathInfo();

        System.out.println("HANDLING !!" + appPath);

        switch(appPath){
            case "/locate_members_form" :
                renderChooserPage(request, httpServletResponse);
                break;
        }

    }

    private void renderChooserPage(Request request, HttpServletResponse response)
            throws IOException {

        System.out.println("Doing chooser form");

        VelocityContext vc = new VelocityContext();
        renderVelocityTemplate("/templates/locate_members_form.vtl", vc, response.getWriter());
        request.setHandled(true);
    }

    private void renderVelocityTemplate(String templatePath, VelocityContext vc, Writer writer){
        Velocity.init();

        InputStream in = this.getClass().getResourceAsStream(templatePath);

        Velocity.evaluate(vc, writer , "", new InputStreamReader(in));
    }

}
