package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;

public abstract class AbstractLegislatorBillEvent extends AbstractBillEvent {

    private final String rawLegislatorName;
    private final BillActionType billActionType;

    public AbstractLegislatorBillEvent(BillEvent underlyingEvent,
                                       String rawLegislatorName,
                                       BillActionType billActionType) {
        super(underlyingEvent);
        this.rawLegislatorName = rawLegislatorName;
        this.billActionType = billActionType;
    }

    @Override
    public String getRawLegislatorName() {
        return rawLegislatorName;
    }

    @Override
    public BillActionType getBillActionType() {
        return billActionType;
    }

}
