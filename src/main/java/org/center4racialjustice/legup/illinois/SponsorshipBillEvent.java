package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;

public class SponsorshipBillEvent extends AbstractLegislatorBillEvent implements BillEventData {
    public SponsorshipBillEvent(BillEvent underlyingEvent, String rawName){
        super(underlyingEvent, rawName, BillActionType.SPONSOR);
    }
}
