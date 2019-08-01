package org.center4racialjustice.legup.domain;

import com.google.common.collect.Multimap;
import org.center4racialjustice.legup.util.LookupTable;

import java.util.List;
import java.util.Map;

public class ReportCardLegislatorAnalysis {

    private final ReportCard card;
    private final Legislator legislator;
    private final Grade grade;
    private final Multimap<Bill,LegislatorBillAction> actions;

    public ReportCardLegislatorAnalysis(ReportCard card, Legislator legislator, Grade grade, Multimap<Bill,LegislatorBillAction> actions) {
        this.card = card;
        this.legislator = legislator;
        this.grade = grade;
        this.actions = actions;
        System.out.println("ACTIONS " + actions);
    }

    public Legislator getLegislator() {
        return legislator;
    }

    public Grade getGrade() {
        return grade;
    }

    public LookupTable<Bill, LegislatorBillActionType, String> supportedBillDetails(){
        LookupTable<Bill, LegislatorBillActionType, String> table = new LookupTable<>();

        for(Bill bill : card.supportedBills()){
            for(LegislatorBillAction action : actions.get(bill)){
                if( action.isVote() ){
                    String voteNote = action.getVoteSide().equals(VoteSide.Yea) ?
                            "Good" : "Bad";
                    table.put(bill, LegislatorBillActionType.VOTE, voteNote);
                } else {
                    table.put(bill, action.getLegislatorBillActionType(), "Good");
                }
            }
        }

        return table;
    }

    public LookupTable<Bill, LegislatorBillActionType, String> opposedBillDetails(){
        LookupTable<Bill, LegislatorBillActionType, String> table = new LookupTable<>();

        for(Bill bill : card.opposedBills()){
            for(LegislatorBillAction action : actions.get(bill)){
                    if( action.isVote() ){
                        String voteNote = action.getVoteSide().equals(VoteSide.Nay) ?
                                "Good" : "Bad";
                        table.put(bill, LegislatorBillActionType.VOTE, voteNote);
                    } else {
                        table.put(bill, action.getLegislatorBillActionType(), "Bad");
                    }
            }
        }

        return table;
    }

}
