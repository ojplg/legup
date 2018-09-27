package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;

public class ResponderHandler implements Handler {

    private final Responder responder;

    public ResponderHandler(Responder responder){
        this.responder = responder;
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {
        LegupSubmission legupSubmission = new LegupSubmission(legupSession, request);
        LegupResponse legupResponse = responder.handle(legupSubmission);
        httpServletResponse.setHeader("Content-Type", legupResponse.getContentType());
        return legupResponse.getVelocityContext();
    }

    public String getRouteName(){
        return responder.getClass().getSimpleName();
    }
}
