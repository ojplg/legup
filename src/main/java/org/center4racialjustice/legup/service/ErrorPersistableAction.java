package org.center4racialjustice.legup.service;

import java.util.Collections;
import java.util.List;

public class ErrorPersistableAction implements PersistableAction {

    private final String error;

    public ErrorPersistableAction(String error) {
        this.error = error;
    }

    @Override
    public String getDisplay() {
        return "ERROR: " + error;
    }

    @Override
    public List<String> getErrors() {
        return Collections.singletonList(error);
    }
}
