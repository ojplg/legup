package org.center4racialjustice.legup.web;

public interface SecuredResponder extends Responder {
    default boolean isSecured() {
        return true;
    }
    default boolean permitted(LegupSubmission legupSubmission) { return false; }
}
