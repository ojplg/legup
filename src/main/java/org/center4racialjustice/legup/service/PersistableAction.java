package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;

import java.util.List;

public interface PersistableAction {

    String getDisplay();
    List<String> getErrors();
    default boolean hasError(){ return getErrors().size() > 0; }

    BillAction asBillAction(BillActionLoad persistedLoad);
}
