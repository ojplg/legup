package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;

public class LegupResponse {

    // 1 template name
    // 2 use container
    // 3 content type

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
