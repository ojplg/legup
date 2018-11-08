package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.domain.User;

import java.util.HashMap;
import java.util.Map;

public class ContinueLegupResponse implements LegupResponse {

    private final Map<String,String> parameters = new HashMap<>();
    private final String nextResponder;

    public ContinueLegupResponse(Class nextResponderClass){
        this.nextResponder = "/" + Util.classNameToLowercaseWithUnderlines(nextResponderClass);
    }

    public void setParameter(String key, String value){
        this.parameters.put(key, value);
    }

    public Map<String,String> getParameters(){
        return parameters;
    }

    @Override
    public VelocityContext getVelocityContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean useContainer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String actionKey() {
        return nextResponder;
    }

    @Override
    public boolean shouldRender() {
        return false;
    }

    public void setUser(User user){
            // does nothing
    }

}
