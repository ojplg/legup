package org.center4racialjustice.legup.domain;

import java.util.List;
import java.util.Map;

public class ReportCardBillAnalysis {

    private final Bill bill;
    private final BillActionSummary billActionSummary;
    private final Map<Legislator, Grade> legislatorScores;

    public ReportCardBillAnalysis(Bill bill, List<BillAction> billActions, Map<Legislator, Grade> legislatorScores){
        this.bill = bill;
        this.billActionSummary = new BillActionSummary(billActions);
        this.legislatorScores = legislatorScores;
    }

    public Bill getBill(){
        return bill;
    }
}
