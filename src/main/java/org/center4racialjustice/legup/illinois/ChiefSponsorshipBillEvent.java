package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEventData;

public class ChiefSponsorshipBillEvent extends AbstractBillEvent implements BillEventData {

    private final String rawLegislatorName;

    public ChiefSponsorshipBillEvent(String rawData, String rawName){
        super(rawData);
        this.rawLegislatorName = rawName;
    }

    @Override
    public BillActionType getBillActionType() {
        return BillActionType.CHIEF_SPONSOR;
    }

    @Override
    public boolean hasLegislator(){
        return true;
    }

    @Override
    public String getRawLegislatorName() {
        return rawLegislatorName;
    }
}
