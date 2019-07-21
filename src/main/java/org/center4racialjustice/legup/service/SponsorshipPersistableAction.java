package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.CompletedBillEventData;
import org.center4racialjustice.legup.domain.Legislator;

public class SponsorshipPersistableAction implements PersistableAction {

    private final CompletedBillEventData completedBillEventData;

    public SponsorshipPersistableAction(CompletedBillEventData completedBillEventData) {
        if( ! (completedBillEventData.isChiefSponsorship() || completedBillEventData.isSponsorship() ) ){
            throw new RuntimeException("Not a sponsorship: " + completedBillEventData);
        }
        this.completedBillEventData = completedBillEventData;
    }

    @Override
    public String getDisplay() {
        Legislator legislator = completedBillEventData.getLegislator();
        if( legislator == null ){
            return "Could not recognize " + completedBillEventData.getRawLegislatorName()
                    + " which parsed to " + completedBillEventData.getParsedLegislatorName()
                    + " and had member ID " + completedBillEventData.getLegislatorMemberID();
        }
        return completedBillEventData.getBillActionType() + " for " + legislator.getDisplay();
    }
}
