package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.LookupTable;

import java.util.List;

public class ReportCardLegislatorAnalysis {

    private final ReportCard card;
    private final Legislator legislator;
    private final Grade grade;
    private final List<BillAction> actions;

    public ReportCardLegislatorAnalysis(ReportCard card, Legislator legislator, Grade grade, List<BillAction> actions) {
        this.card = card;
        this.legislator = legislator;
        this.grade = grade;
        this.actions = actions;
    }

    public Legislator getLegislator() {
        return legislator;
    }

    public Grade getGrade() {
        return grade;
    }

    public LookupTable<Bill, BillActionType, String> supportedBillDetails(){
        LookupTable<Bill, BillActionType, String> table = new LookupTable<>();

        for(Bill bill : card.supportedBills()){
            for(BillAction action : actions){
                if( action.getBill().equals(bill)){
                    if( action.isVote() ){
                        String voteNote = action.getBillActionDetail().equals(VoteSide.YeaCode) ?
                                "Good" : "Bad";
                        table.put(bill, BillActionType.VOTE, voteNote);
                    } else {
                        table.put(bill, action.getBillActionType(), "Good");
                    }
                }
            }
        }

        return table;
    }

    public LookupTable<Bill, BillActionType, String> opposedBillDetails(){
        LookupTable<Bill, BillActionType, String> table = new LookupTable<>();

        for(Bill bill : card.opposedBills()){
            for(BillAction action : actions){
                if( action.getBill().equals(bill)){
                    if( action.isVote() ){
                        String voteNote = action.getBillActionDetail().equals(VoteSide.NayCode) ?
                                "Good" : "Bad";
                        table.put(bill, BillActionType.VOTE, voteNote);
                    } else {
                        table.put(bill, action.getBillActionType(), "Bad");
                    }
                }
            }
        }

        return table;
    }

}
