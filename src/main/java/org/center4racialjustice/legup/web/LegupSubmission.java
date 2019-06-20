package org.center4racialjustice.legup.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.User;
import org.hrorm.Converter;
import org.eclipse.jetty.server.Request;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class LegupSubmission {

    private final Logger log = LogManager.getLogger(LegupSubmission.class);

    private final LegupSession legupSession;
    private final Map<String,String> parameters;

    public LegupSubmission(LegupSession legupSession, Request request) {
        this.legupSession = legupSession;
        this.parameters = new HashMap<>();

        Enumeration<String> parameterNames = request.getParameterNames();
        while( parameterNames.hasMoreElements()){
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            parameters.put(name, value);
        }
    }

    private LegupSubmission(LegupSession legupSession, Map<String, String> parameters){
        this.legupSession = legupSession;
        this.parameters = parameters;
    }

    public LegupSubmission update(Map<String, String> parameters){
        return new LegupSubmission(legupSession, parameters);
    }

    public Map<String, String> getParameters(){
        return parameters;
    }

    public <T> T getConvertedParameter(String parameterName, Converter<T, String> converter){
        String parameterValueString = internalGetParameter(parameterName);
        if ( parameterValueString != null ){
            return converter.to(parameterValueString);
        }
        return null;
    }

    public Long getLongRequestParameter(String parameterName){
        String parameterValueString = internalGetParameter(parameterName);
        if ( parameterValueString != null && parameterValueString.length() > 0 ){
            return Long.parseLong(parameterValueString);
        }
        return null;
    }

    public boolean isNonEmptyStringParameter(String parameterName){
        String parameterValue = internalGetParameter(parameterName);
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
        return internalGetParameter(parameterName);
    }

    public String setObject(String key, Object item){
        return legupSession.setObject(key, item);
    }

    public Object getObject(String key){
        String oneTimeKey = internalGetParameter("one_time_key");
        return legupSession.getObject(key, oneTimeKey);
    }

    public Map<Long, String> extractNumberedParameters(String prefix){
        Map<Long, String> parameterValues = new HashMap<>();
        for(String parameterName : parameters.keySet() ) {
            if (parameterName.startsWith(prefix)){
                String numberString = parameterName.substring(prefix.length());
                Long number = Long.parseLong(numberString);
                String value = parameters.get(parameterName);
                parameterValues.put(number, value);
            }
        }
        return parameterValues;
    }

    private String internalGetParameter(String parameterName){
        String value = parameters.get(parameterName);
        return value;
    }


    public void setLoggedInUser(User user){
        this.legupSession.setLoggedInUser(user);
    }

    public User getLoggedInUser() { return this.legupSession.getLoggedInUser(); }

    public boolean isSuperUserRequest() { return this.legupSession.isLoggedInSuperUser(); }

    public boolean isLoggedIn() {
        return legupSession.getLoggedInUser() != null;
    }

    public void logout(){
        legupSession.logout();
    }

    public Organization getOrganization(){
        return legupSession.getLoggedInUser().getOrganization();
    }
}
