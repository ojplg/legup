package org.center4racialjustice.legup.domain;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
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
    private final Grader houseGrader;
    private final Grader senateGrader;
    private final Map<Legislator, Grade> houseGrades;
    private final Map<Legislator, Grade> senateGrades;

    public ReportCardGrades(ReportCard reportCard, List<BillAction> actions){
        this.reportCard = reportCard;
        this.actions = actions;

        this.lookupTable = reportCard.calculateScores(actions);

        Map<Legislator, Integer> houseSums = sumScores(Chamber.House);
        this.houseGrader = new Grader(reportCard.getGradeLevels(), houseSums.values());

        Map<Legislator, Integer> senateSums = sumScores(Chamber.Senate);
        this.senateGrader = new Grader(reportCard.getGradeLevels(), senateSums.values());

        this.houseGrades = assignGrades(houseSums, houseGrader);
        this.senateGrades = assignGrades(senateSums, senateGrader);
    }

    public ReportCard getReportCard() { return  reportCard;}

    public String getReportCardName(){
        return reportCard.getName();
    }

    public LookupTable<Legislator, Bill, Integer> getLookupTable() {
        return lookupTable;
    }

    public Map<Legislator, Grade> getGrades(){
        Map<Legislator, Grade> allGrades = new HashMap<>();
        allGrades.putAll(houseGrades);
        allGrades.putAll(senateGrades);
        return allGrades;
    }

    public List<Bill> getBills(){
        return lookupTable.sortedColumnHeadings(Bill::compareTo);
    }

    public List<Legislator> getLegislators(){
        return lookupTable.sortedRowHeadings(Legislator::compareTo);
    }

    private Map<Legislator, Grade> assignGrades(Map<Legislator, Integer> sums, Grader grader){
        Map<Legislator, Grade> grades = new HashMap<>();
        for( Map.Entry<Legislator, Integer> entry : sums.entrySet()){
            Grade grade = grader.assignGrade(entry.getKey().getChamber(), entry.getValue());
            grades.put(entry.getKey(), grade);
        }
        return grades;
    }

    public ReportCardBillAnalysis getBillAnalysis(long billId){
        ReportFactor reportFactor = Lists.findfirst(reportCard.getReportFactors(), f -> f.getBill().getId().equals(billId));
        List<BillAction> billActions = actions.stream()
                .filter(a -> billId == a.getBill().getId())
                .collect(Collectors.toList());
        return new ReportCardBillAnalysis(reportFactor, billActions, getGrades());
    }

    public ReportCardLegislatorAnalysis getLegislatorAnalysis(long legislatorId){
        Legislator legislator = Lists.findfirst(
                new ArrayList<>(getGrades().keySet()),
                l -> l.getId() == legislatorId );
        Grade grade = getGrades().get(legislator);
        Multimap<Bill,LegislatorBillAction> legislatorActions = MultimapBuilder.hashKeys().arrayListValues().build();
        for( BillAction action : actions){
            LegislatorBillAction legislatorBillAction = action.getLegislatorAction(legislator);
            if ( legislatorBillAction != null && legislatorBillAction.getLegislator().equals(legislator)){
                legislatorActions.put(action.getBill(), legislatorBillAction);
            }
        }
        return new ReportCardLegislatorAnalysis(reportCard, legislator, grade, legislatorActions);
    }

    private Map<Legislator, Integer> sumScores(Chamber chamber) {
        Map<Legislator, Integer> sums = new HashMap<>();

        for (Legislator legislator : lookupTable.getRowHeadings()) {
            if( legislator.getChamber().equals(chamber)) {
                Integer sum = lookupTable.computeRowSummary(legislator, 0, ReportCard.ScoreComputer);
                sums.put(legislator, sum);
            }
        }

        return sums;
    }

    public int getLowHouseScore() {
        return houseGrader.getLowScore();
    }

    public int getHighHouseScore() {
        return houseGrader.getHighScore();
    }

    public int getHouseMean() {
        return houseGrader.getMean();
    }

    public int getLowSenateScore() {
        return senateGrader.getLowScore();
    }

    public int getHighSenateScore() {
        return senateGrader.getHighScore();
    }

    public int getSenateMean() {
        return senateGrader.getMean();
    }

}
