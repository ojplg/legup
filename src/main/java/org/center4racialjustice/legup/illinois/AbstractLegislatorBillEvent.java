package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.Name;

public abstract class AbstractLegislatorBillEvent extends AbstractBillEvent {

    private final String rawLegislatorName;
    private final Name parsedLegislatorName;
    private final BillActionType billActionType;

    public AbstractLegislatorBillEvent(BillEvent underlyingEvent,
                                       String rawLegislatorName,
                                       Name parsedLegislatorName,
                                       BillActionType billActionType) {
        super(underlyingEvent);
        this.rawLegislatorName = rawLegislatorName;
        this.parsedLegislatorName = parsedLegislatorName;
        this.billActionType = billActionType;
    }

    @Override
    public String getRawLegislatorName() {
        return rawLegislatorName;
    }

    @Override
    public Name getParsedLegislatorName() { return parsedLegislatorName; }

    @Override
    public BillActionType getBillActionType() {
        return billActionType;
    }

}
