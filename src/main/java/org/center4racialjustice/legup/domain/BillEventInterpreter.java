package org.center4racialjustice.legup.domain;

public interface BillEventInterpreter {
    BillEvent parse(RawBillEvent rawBillEvent);
}
