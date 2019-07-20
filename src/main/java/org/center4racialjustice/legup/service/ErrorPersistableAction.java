package org.center4racialjustice.legup.service;

public class ErrorPersistableAction implements PersistableAction {

    private final String error;

    public ErrorPersistableAction(String error) {
        this.error = error;
    }

    @Override
    public String getDisplay() {
        return "ERROR: " + error;
    }
}
