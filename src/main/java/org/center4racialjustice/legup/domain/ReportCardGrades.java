package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.LookupTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportCardGrades {

    private final ReportCard reportCard;

    private final LookupTable<Legislator, Bill, Integer> lookupTable;
    private final Map<Legislator, Grade> grades;
    private final List<BillAction> actions;

    public ReportCardGrades(ReportCard reportCard, List<BillAction> actions){
        this.reportCard = reportCard;
        this.actions = actions;

        this.lookupTable = reportCard.calculateScores(actions);
        this.grades = assignGrades();
    }

    public String getReportCardName(){
        return reportCard.getName();
    }

    public LookupTable<Legislator, Bill, Integer> getLookupTable() {
        return lookupTable;
    }

    public Map<Legislator, Grade> getGrades(){
        return grades;
    }

    public List<Bill> getBills(){
        return lookupTable.sortedColumnHeadings(Bill::compareTo);
    }

    public List<Legislator> getLegislators(){
        return lookupTable.sortedRowHeadings(Legislator::compareTo);
    }

    private Map<Legislator, Grade> assignGrades(){
        Map<Legislator, Integer> sums = sumScores();
        Integer min = findMin(sums);
        Integer max = findMax(sums);
        Grader grader = new Grader(min, max);

        Map<Legislator, Grade> grades = new HashMap<>();
        for( Map.Entry<Legislator, Integer> entry : sums.entrySet()){
            Grade grade = grader.assignGrade(entry.getValue());
            grades.put(entry.getKey(), grade);
        }

        return grades;
    }

    public ReportCardBillAnalysis getBillAnalysis(long billId){
        ReportFactor reportFactor = Lists.findfirst(reportCard.getReportFactors(), f -> f.getBill().getId().equals(billId));
        List<BillAction> billActions = actions.stream()
                .filter(a -> billId == a.getBill().getId())
                .collect(Collectors.toList());
        return new ReportCardBillAnalysis(reportFactor, billActions, grades);
    }

    private Map<Legislator, Integer> sumScores() {
        Map<Legislator, Integer> sums = new HashMap<>();

        for (Legislator legislator : lookupTable.getRowHeadings()) {
            Integer sum = lookupTable.computeRowSummary(legislator, 0, ReportCard.ScoreComputer);
            sums.put(legislator, sum);
        }

        return sums;
    }

    private static Integer findMax(Map<Legislator, Integer> sums){
        Integer max = Integer.MIN_VALUE;
        for(Integer amount : sums.values()){
            if (amount > max){
                max = amount;
            }
        }
        return max;
    }

    private static Integer findMin(Map<Legislator, Integer> sums){
        Integer min = Integer.MAX_VALUE;
        for(Integer amount : sums.values()){
            if (amount < min){
                min = amount;
            }
        }
        return min;
    }

}
