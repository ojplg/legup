package org.center4racialjustice.legup.web;

import org.center4racialjustice.legup.db.hrorm.Converter;
import org.eclipse.jetty.server.Request;

import java.util.Enumeration;

public class LegupSubmission {

    private final LegupSession legupSession;
    private final Request request;

    public LegupSubmission(LegupSession legupSession, Request request) {
        this.legupSession = legupSession;
        this.request = request;
    }

    public <T> T getConvertedParameter(String parameterName, Converter<T, String> converter){
        String parameterValueString = request.getParameter(parameterName);
        if ( parameterValueString != null ){
            return converter.to(parameterValueString);
        }
        return null;
    }

    public Long getLongRequestParameter(String parameterName){
        String parameterValueString = request.getParameter(parameterName);
        if ( parameterValueString != null && parameterValueString.length() > 0 ){
            return Long.parseLong(parameterValueString);
        }
        return null;
    }

    public String getParameter(String parameterName){
        return request.getParameter(parameterName);
    }

    public String setObject(String key, Object item){
        return legupSession.setObject(key, item);
    }

    public Object getObject(String key){
        String oneTimeKey = request.getParameter("one_time_key");
        return legupSession.getObject(key, oneTimeKey);
    }

    public Enumeration<String> getParameterNames(){
        return request.getParameterNames();
    }
}
