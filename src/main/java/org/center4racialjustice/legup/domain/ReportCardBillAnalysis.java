package org.center4racialjustice.legup.domain;

import java.util.List;
import java.util.Map;

public class ReportCardBillAnalysis {

    private final ReportFactor reportFactor;
    private final BillActionSummary billActionSummary;
    private final Map<Legislator, Grade> legislatorScores;

    public ReportCardBillAnalysis(ReportFactor reportFactor, List<BillAction> billActions, Map<Legislator, Grade> legislatorScores){
        this.reportFactor = reportFactor;
        this.billActionSummary = new BillActionSummary(billActions);
        this.legislatorScores = legislatorScores;
    }

    public VoteSide getDesiredOutcome(){
        return reportFactor.getVoteSide();
    }

    public Bill getBill(){
        return reportFactor.getBill();
    }

    public BillActionSummary getBillActionSummary() {
        return billActionSummary;
    }
}
