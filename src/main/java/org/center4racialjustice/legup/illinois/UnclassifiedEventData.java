package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEventData;

public class UnclassifiedEventData extends AbstractBillEvent implements BillEventData {

    public UnclassifiedEventData(String rawContents){
        super(rawContents);
    }

    @Override
    public BillActionType getBillActionType() {
        return BillActionType.UNCLASSIFIED;
    }

    @Override
    public String getRawLegislatorName() {
        return null;
    }
}
