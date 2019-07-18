package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEventData;

public class SponsorshipBillEvent extends AbstractLegislatorBillEvent implements BillEventData {
    public SponsorshipBillEvent(String rawData, String rawName){
        super(rawData, rawName, BillActionType.SPONSOR);
    }
}
