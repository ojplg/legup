package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;

import java.util.Map;

public interface LegupResponse {
    VelocityContext getVelocityContext();

    boolean useContainer();

    String getContentType();

    String actionKey();

    boolean shouldRender();

    Map<String,String> getParameters();
}
