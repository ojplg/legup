package org.center4racialjustice.legup.web;

import lombok.Data;
import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.domain.User;

import java.util.ArrayList;
import java.util.List;

@Data
public class TopMatter {

    private final User user;
    private final List<NavLink> links;
    private final String helpLink;
    private final List<String> renderedLinks = new ArrayList<>();

    public void renderLinks(VelocityContext velocityContext){
        for( NavLink link : links ){
            renderedLinks.add(link.renderLink(velocityContext));
        }
    }
}
