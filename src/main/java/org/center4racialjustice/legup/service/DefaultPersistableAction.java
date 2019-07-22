package org.center4racialjustice.legup.service;

import java.util.Collections;
import java.util.List;

public class DefaultPersistableAction implements PersistableAction {
    @Override
    public String getDisplay() {
        return "default";
    }

    @Override
    public List<String> getErrors() {
        return Collections.emptyList();
    }
}
