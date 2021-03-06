package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.CompletedBillEvent;
import org.center4racialjustice.legup.service.PersistableAction;

import java.util.ArrayList;
import java.util.List;

public class VoteResultsEventDisplay implements PersistableAction {

    private static final Logger log = LogManager.getLogger(VoteResultsEventDisplay.class);

    private final CompletedBillEvent completedBillEvent;
    private final BillVotesResults billVotesResults;

    public VoteResultsEventDisplay(CompletedBillEvent completedBillEvent, BillVotesResults billVotesResults) {
        this.completedBillEvent = completedBillEvent;
        if( billVotesResults == null ){
            log.warn("No matching bill vote results for " + completedBillEvent);
            this.billVotesResults = BillVotesResults.EMPTY;
        } else {
            this.billVotesResults = billVotesResults;
        }
    }

    @Override
    public String getDisplay() {
        StringBuilder buf = new StringBuilder();
        if( completedBillEvent.hasCommittee() ){
            buf.append(completedBillEvent.getCommittee());
            buf.append("<br/>");
        } else {
            buf.append("Full chamber");
            buf.append("<br/>");
        }
        buf.append(billVotesResults.getDisplay());
        return buf.toString();
    }

    @Override
    public List<String> getErrors() {
        List<String> errors = new ArrayList<>();
        errors.addAll(billVotesResults.getErrors());
        errors.addAll(completedBillEvent.getErrors());
        return errors;
    }

    @Override
    public BillAction asBillAction(BillActionLoad persistedLoad) {
        BillAction billAction = completedBillEvent.asBillAction(persistedLoad);
        billAction.setLegislatorBillActions(billVotesResults.asLegislatorActions());
        return billAction;
    }
}
