package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.LookupTable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

@Data
public class ReportCard {

    public static BinaryOperator<Integer> ScoreComputer = (i, j) -> i + j;

    private Long id;
    private String name;
    private long sessionNumber;
    private List<ReportFactor> reportFactors = new ArrayList<>();

    public void addReportFactor(ReportFactor factor){
        factor.setReportCardId(id);
        reportFactors.add(factor);
    }

    public ReportFactor findByBill(Bill bill){
        return Lists.findfirst(reportFactors, f -> f.getBill().equals(bill));
    }

    public LookupTable<Legislator, Bill, Integer> calculateScores(List<BillAction> actions) {
        LookupTable<Legislator, Bill, Integer> scoreTable = new LookupTable<>(0);

        for( BillAction action : actions ){
            Bill bill = action.getBill();
            ReportFactor factor = findByBill(action.getBill());
            if( factor == null ){
                continue;
            }

            int score = action.score(factor.getVoteSide());
            scoreTable.merge(action.getLegislator(), bill, score, ScoreComputer);
        }

        return scoreTable;
    }

}
