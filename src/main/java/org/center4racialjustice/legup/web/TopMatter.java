package org.center4racialjustice.legup.web;

import lombok.Builder;
import lombok.Data;
import org.center4racialjustice.legup.domain.User;

import java.util.List;

@Data
public class TopMatter {

    private final User user;
    private final List<NavLink> links;
    private final String helpLink;

}
