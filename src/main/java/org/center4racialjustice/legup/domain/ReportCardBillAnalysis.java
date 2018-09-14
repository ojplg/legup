package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.Tuple;

import java.util.ArrayList;
import java.util.Comparator;
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

    public List<Tuple<Grade, Legislator>> getGradedLegislators(Chamber chamber, VoteSide voteSide){
        List<Vote> votes = billActionSummary.getVotes(chamber, voteSide);
        List<Tuple<Grade, Legislator>> gradedLegislators = new ArrayList<>();
        for(Vote vote : votes){
            Legislator legislator = vote.getLegislator();
            Grade grade = legislatorScores.get(legislator);
            Tuple<Grade, Legislator> tuple = new Tuple<>(grade, legislator);
            gradedLegislators.add(tuple);
        }
        Comparator<Tuple<Grade,Legislator>> comparator = Comparator.comparing(Tuple::getFirst);
        gradedLegislators.sort(comparator.reversed());
        return gradedLegislators;
    }

}
