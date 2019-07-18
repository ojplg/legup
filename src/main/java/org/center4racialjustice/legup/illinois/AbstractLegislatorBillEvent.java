package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;

public abstract class AbstractLegislatorBillEvent extends AbstractBillEvent {

    private final String rawLegislatorName;
    private final BillActionType billActionType;

    public AbstractLegislatorBillEvent(String rawData,
                                       String rawLegislatorName,
                                       BillActionType billActionType) {
        super(rawData);
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
