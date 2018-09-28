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

    public String getParameter(String parameterName){
        return request.getParameter(parameterName);
    }

    public String setObject(String key, Object item){
        return legupSession.setObject(key, item);
    }

    public Object getObject(String key){
        String oneTimeKey = request.getParameter("oneTimeKey");
        return legupSession.getObject(key, oneTimeKey);
    }
}
