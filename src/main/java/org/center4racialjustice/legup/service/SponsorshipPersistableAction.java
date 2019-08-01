package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.CompletedBillEvent;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.LegislatorBillAction;
import org.center4racialjustice.legup.domain.LegislatorBillActionType;

import java.util.Collections;
import java.util.List;

public class SponsorshipPersistableAction implements PersistableAction {

    private final CompletedBillEvent completedBillEventData;

    public SponsorshipPersistableAction(CompletedBillEvent completedBillEventData) {
        if( ! BillActionType.isSponsoringRelated(completedBillEventData.getBillActionType()) ){
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
        StringBuilder buf = new StringBuilder();

        buf.append(completedBillEventData.getChamber());
        buf.append("<br//>");
        buf.append(completedBillEventData.getBillActionType().getCode());
        buf.append("<br//>");
        buf.append(completedBillEventData.getLegislator().getDisplay());
        buf.append("<br//>");

        return buf.toString();
    }

    @Override
    public List<String> getErrors() {
        return completedBillEventData.getErrors();
    }

    @Override
    public BillAction asBillAction(BillActionLoad persistedLoad) {
        BillAction billAction = completedBillEventData.asBillAction(persistedLoad);

        LegislatorBillAction legislatorBillAction = new LegislatorBillAction();
        LegislatorBillActionType legislatorBillActionType = LegislatorBillActionType.fromBillActionType(billAction.getBillActionType());
        legislatorBillAction.setLegislatorBillActionType(legislatorBillActionType);
        legislatorBillAction.setLegislator(completedBillEventData.getLegislator());

        billAction.setLegislatorBillActions(Collections.singletonList(legislatorBillAction));

        return billAction;
    }
}
