package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;

import java.util.Map;

public class PlainTextLegupResponse implements LegupResponse {

    private final String templateName;
    private final VelocityContext velocityContext;

    public PlainTextLegupResponse(Class responderClass){
        this.templateName = Util.classNameToLowercaseWithUnderlines(responderClass) + ".vtl";
        this.velocityContext = new VelocityContext();
    }

    @Override
    public VelocityContext getVelocityContext() {
        return velocityContext;
    }

    public void putVelocityData(String key, Object value){
        velocityContext.put(key, value);
    }

    @Override
    public boolean useContainer() {
        return false;
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override
    public String actionKey() {
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
}
