package org.center4racialjustice.legup.domain;

public interface BillEventInterpreter {

    BillActionType readActionType(BillEvent billEvent);

}
