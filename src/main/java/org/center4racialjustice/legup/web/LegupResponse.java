package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;

public interface LegupResponse {
    VelocityContext getVelocityContext();

    boolean useContainer();

    String getContentType();

    String getTemplateName();
}
