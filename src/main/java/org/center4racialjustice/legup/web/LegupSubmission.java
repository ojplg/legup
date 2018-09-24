package org.center4racialjustice.legup.web;

import org.eclipse.jetty.server.Request;

public class LegupSubmission {

    private final LegupSession legupSession;
    private final Request request;

    public LegupSubmission(LegupSession legupSession, Request request) {
        this.legupSession = legupSession;
        this.request = request;
    }

    public Long getLongRequestParameter(String parameterName){
        return Util.getLongParameter(request, parameterName);
    }
}
