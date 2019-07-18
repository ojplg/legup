package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;

public class CommitteeAmendmentFiledBillEvent extends AbstractLegislatorBillEvent {
    public CommitteeAmendmentFiledBillEvent(String rawData, String rawLegislatorName) {
        super(rawData, rawLegislatorName, BillActionType.COMMITTEE_AMENDMENT_FILED);
    }
}
