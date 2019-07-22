package org.center4racialjustice.legup.service;

import java.util.List;

public interface PersistableAction {

    String getDisplay();
    List<String> getErrors();
    default boolean hasError(){ return getErrors().size() > 0; }

}
