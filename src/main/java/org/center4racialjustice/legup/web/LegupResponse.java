package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;

public class LegupResponse {

    private final VelocityContext velocityContext;

    public LegupResponse() {
        this.velocityContext = new VelocityContext();
    }

    public void putVelocityData(String key, Object value){
        velocityContext.put(key, value);
    }

    public VelocityContext getVelocityContext() {
        return velocityContext;
    }
}
