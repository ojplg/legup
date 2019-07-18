package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;

public class ChiefSponsorshipBillEvent extends AbstractLegislatorBillEvent implements BillEventData {
    public ChiefSponsorshipBillEvent(BillEvent underlyingEvent, String rawName){
        super(underlyingEvent, rawName, BillActionType.CHIEF_SPONSOR);
    }
}
