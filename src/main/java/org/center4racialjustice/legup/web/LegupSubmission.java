package org.center4racialjustice.legup.web;

import org.center4racialjustice.legup.db.hrorm.Converter;
import org.eclipse.jetty.server.Request;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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

    public boolean isNonEmptyStringParameter(String parameterName){
        String parameterValue = request.getParameter(parameterName);
        return parameterValue != null && parameterValue.trim().length() > 0;
    }

    public boolean isValidLongParameter(String parameterName){
        try {
            Long value = getLongRequestParameter(parameterName);
            return value != null;
        } catch (NumberFormatException ex){
            return false;
        }
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

    public Map<Long, String> extractNumberedParameters(String prefix){
        Map<Long, String> parameterValues = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while(parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if (parameterName.startsWith(prefix)){
                String numberString = parameterName.substring(prefix.length());
                Long number = Long.parseLong(numberString);
                String value = request.getParameter(parameterName);
                parameterValues.put(number, value);
            }
        }
        return parameterValues;
    }

}
