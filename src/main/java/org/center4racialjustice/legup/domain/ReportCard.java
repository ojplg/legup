package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.LookupTable;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@Data
public class ReportCard {

    public static BinaryOperator<Integer> ScoreComputer = (i, j) -> i + j;

    private Long id;
    private String name;
    private long sessionNumber;
    private List<ReportFactor> reportFactors = new ArrayList<>();
    private List<ReportCardLegislator> reportCardLegislators = new ArrayList<>();

    public void addReportFactor(ReportFactor factor){
        factor.setReportCardId(id);
        reportFactors.add(factor);
    }

    public void addReportCardLegislator(ReportCardLegislator legislator){
        legislator.setReportCardId(id);
        reportCardLegislators.add(legislator);
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

    public List<Bill> supportedBills(){
        return reportFactors.stream()
                .filter(f -> f.getVoteSide().equals(VoteSide.Yea))
                .map(f -> f.getBill())
                .collect(Collectors.toList());
    }

    public List<Bill> opposedBills(){
        return reportFactors.stream()
                .filter(f -> f.getVoteSide().equals(VoteSide.Nay))
                .map(f -> f.getBill())
                .collect(Collectors.toList());
    }

    public SortedMap<Legislator, Boolean> findSelectedLegislators(List<Legislator> legislators){
        TreeMap<Legislator, Boolean> map = new TreeMap<>();

        if( reportCardLegislators.isEmpty() ){
            for (Legislator legislator : legislators){
                map.put(legislator, legislator.getCompleteTerm());
            }
        } else {
            for (Legislator legislator : legislators) {
                if (reportCardLegislators.stream().anyMatch(rcl -> rcl.getLegislator().equals(legislator))) {
                    map.put(legislator, Boolean.TRUE);
                } else {
                    map.put(legislator, Boolean.FALSE);
                }
            }
        }
        return map;
    }

}
