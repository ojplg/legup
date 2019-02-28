package org.center4racialjustice.legup.web;

public interface Responder {
    LegupResponse handle(LegupSubmission submission);

    default boolean isSecured() {
        return false;
    }
}
