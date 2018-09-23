package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.LookupTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportCardGrades {

    private final ReportCard reportCard;
    private final List<BillAction> actions;
    private final LookupTable<Legislator, Bill, Integer> lookupTable;
    private final Grader grader;
    private final Map<Legislator, Grade> grades;

    public ReportCardGrades(ReportCard reportCard, List<BillAction> actions){
        this.reportCard = reportCard;
        this.actions = actions;

        this.lookupTable = reportCard.calculateScores(actions);

        Map<Legislator, Integer> sums = sumScores();
        this.grader = new Grader(sums.values());
        this.grades = assignGrades(sums);
    }

    public ReportCard getReportCard() { return  reportCard;}

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

    private Map<Legislator, Grade> assignGrades(Map<Legislator, Integer> sums){
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

    public ReportCardLegislatorAnalysis getLegislatorAnalysis(long legislatorId){
        Legislator legislator = Lists.findfirst(
                new ArrayList<>(grades.keySet()),
                l -> l.getId() == legislatorId );
        Grade grade = grades.get(legislator);
        List<BillAction> legislatorActions = actions.stream()
                .filter(a -> a.getLegislator().equals(legislator))
                .collect(Collectors.toList());

        return new ReportCardLegislatorAnalysis(reportCard, legislator, grade, legislatorActions);
    }

    private Map<Legislator, Integer> sumScores() {
        Map<Legislator, Integer> sums = new HashMap<>();

        for (Legislator legislator : lookupTable.getRowHeadings()) {
            Integer sum = lookupTable.computeRowSummary(legislator, 0, ReportCard.ScoreComputer);
            sums.put(legislator, sum);
        }

        return sums;
    }

    public int getLowScore() {
        return grader.getLowScore();
    }

    public int getHighScore() {
        return grader.getHighScore();
    }

    public int getMean() {
        return grader.getMean();
    }

}
