package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;

public class CommitteeAmendmentFiledBillEvent extends AbstractLegislatorBillEvent {
    public CommitteeAmendmentFiledBillEvent(BillEvent underlyingEvent, String rawLegislatorName) {
        // FIXME: Need to parse name!
        super(underlyingEvent, rawLegislatorName, null, BillActionType.COMMITTEE_AMENDMENT_FILED);
    }
}
