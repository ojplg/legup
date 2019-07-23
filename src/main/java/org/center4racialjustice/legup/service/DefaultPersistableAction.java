package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.CompletedBillEvent;

import java.util.Collections;
import java.util.List;

public class DefaultPersistableAction implements PersistableAction {

    private final CompletedBillEvent completedBillEvent;

    public DefaultPersistableAction(CompletedBillEvent completedBillEvent) {
        this.completedBillEvent = completedBillEvent;
    }

    @Override
    public String getDisplay() {
        return "default";
    }

    @Override
    public List<String> getErrors() {
        return Collections.emptyList();
    }

    @Override
    public BillAction asBillAction(BillActionLoad persistedLoad) {
        return completedBillEvent.asBillAction(persistedLoad);
    }
}
