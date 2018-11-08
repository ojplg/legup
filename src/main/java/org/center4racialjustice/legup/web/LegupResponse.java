package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.domain.User;

import java.util.Map;

public interface LegupResponse {
    VelocityContext getVelocityContext();

    boolean useContainer();

    String getContentType();

    String actionKey();

    boolean shouldRender();

    Map<String,String> getParameters();

    void setUser(User user);
}
