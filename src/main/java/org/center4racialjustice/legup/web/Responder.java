package org.center4racialjustice.legup.web;

import java.util.Collections;
import java.util.List;

public interface Responder {
    LegupResponse handle(LegupSubmission submission);

    default List<NavLink> navLinks(){ return Collections.emptyList(); }
    default String helpLink() { return null; }
}
