package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.domain.User;

import java.util.Map;

public class HtmlLegupResponse implements LegupResponse {

    private final VelocityContext velocityContext;
    private final String templateName;

    public static LegupResponse forError(Class formClass, String errorMessage, Map<String, String> errors){
        HtmlLegupResponse legupResponse = new HtmlLegupResponse(formClass);
        legupResponse.putVelocityData("errorMessage", errorMessage);
        for(Map.Entry<String, String> entry : errors.entrySet()){
            legupResponse.putVelocityData(entry.getKey() + "Error", entry.getValue());
        }
        return legupResponse;
    }

    public HtmlLegupResponse(Class responderClass){
        this.velocityContext = new VelocityContext();
        this.templateName = Util.classNameToLowercaseWithUnderlines(responderClass) + ".vtl";
    }

    public void putVelocityData(String key, Object value){
        velocityContext.put(key, value);
    }

    @Override
    public VelocityContext getVelocityContext() {
        return velocityContext;
    }

    @Override
    public boolean useContainer(){
        return true;
    }

    @Override
    public String getContentType(){
        return "text/html";
    }

    @Override
    public String actionKey(){
        return templateName;
    }

    @Override
    public boolean shouldRender() {
        return true;
    }

    @Override
    public Map<String, String> getParameters() {
        throw new UnsupportedOperationException();
    }

    public void setUser(User user){
        putVelocityData("user", user);
    }
}
