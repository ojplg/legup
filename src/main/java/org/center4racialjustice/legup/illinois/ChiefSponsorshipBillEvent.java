package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.Name;

public class ChiefSponsorshipBillEvent extends AbstractLegislatorBillEvent implements BillEventData {
    public ChiefSponsorshipBillEvent(BillEvent underlyingEvent, String rawName, Name parsedName){
        super(underlyingEvent, rawName, parsedName, BillActionType.CHIEF_SPONSOR);
    }
}
