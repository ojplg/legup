package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.Committee;
import org.center4racialjustice.legup.domain.CompletedBillEvent;
import org.center4racialjustice.legup.domain.Legislator;

import java.util.Arrays;
import java.util.List;

public class CommitteePersistableAction implements PersistableAction {

    private static final List<BillActionType> Supported_Types = Arrays.asList(
            BillActionType.COMMITTEE_REFERRAL,
            BillActionType.COMMITTEE_ASSIGNMENT,
            BillActionType.COMMITTEE_POSTPONEMENT
    );

    private final CompletedBillEvent completedBillEventData;

    public static boolean supports(BillActionType billActionType){
        return Supported_Types.contains(billActionType);
    }

    public CommitteePersistableAction(CompletedBillEvent completedBillEventData) {
        if( ! Supported_Types.contains(completedBillEventData.getBillActionType()) ){
            throw new RuntimeException("Not a correct action type: " + completedBillEventData);
        }
        this.completedBillEventData = completedBillEventData;
    }

    @Override
    public String getDisplay() {
        Committee committee = completedBillEventData.getCommittee();
        if( committee == null ){
            return "Could not recognize " + completedBillEventData.getRawCommitteeName()
                    + " and had committee ID " + completedBillEventData.getCommitteeID()
                    + " in " + completedBillEventData;
        }
        StringBuilder buf = new StringBuilder();

        buf.append(completedBillEventData.getChamber());
        buf.append("<br//>");
        buf.append(completedBillEventData.getBillActionType().getCode());
        buf.append("<br//>");
        buf.append(completedBillEventData.getCommittee().getName());
        buf.append("<br//>");

        return buf.toString();
    }

    @Override
    public List<String> getErrors() {
        return completedBillEventData.getErrors();
    }
}
