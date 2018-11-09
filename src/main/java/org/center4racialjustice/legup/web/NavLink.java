package org.center4racialjustice.legup.web;

import lombok.Data;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;

@Data
public class NavLink {
    private final String text;
    private final String href;

    public String renderLink(VelocityContext velocityContext){
        StringWriter stringWriter = new StringWriter();
        velocityContext.put("atext", text);
        velocityContext.put("ahref", href);
        Velocity.mergeTemplate("/templates/alink.vtl", "ISO-8859-1", velocityContext, stringWriter);
        return stringWriter.toString();
    }
}
