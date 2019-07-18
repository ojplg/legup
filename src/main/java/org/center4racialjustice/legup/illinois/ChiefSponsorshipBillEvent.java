package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEventData;

public class ChiefSponsorshipBillEvent extends AbstractLegislatorBillEvent implements BillEventData {
    public ChiefSponsorshipBillEvent(String rawData, String rawName){
        super(rawData, rawName, BillActionType.CHIEF_SPONSOR);
    }
}
