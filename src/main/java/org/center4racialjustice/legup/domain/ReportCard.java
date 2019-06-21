package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.LookupTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private Organization organization;
    private List<GradeLevel> gradeLevelList;

    public void addReportFactor(ReportFactor factor){
        reportFactors.add(factor);
    }

    public void addReportCardLegislator(ReportCardLegislator legislator){
        reportCardLegislators.add(legislator);
    }

    public void setReportFactors(List<ReportFactor> reportFactors) {
        this.reportFactors.clear();
        reportFactors.forEach(this::addReportFactor);
    }

    public void setReportCardLegislators(List<ReportCardLegislator> reportCardLegislators) {
        this.reportCardLegislators.clear();
        reportCardLegislators.forEach(this::addReportCardLegislator);
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
            if ( isIncludedLegislator(action.getLegislator())) {
                scoreTable.merge(action.getLegislator(), bill, score, ScoreComputer);
            }
        }

        return scoreTable;
    }

    private boolean isIncludedLegislator(Legislator legislator){
        return reportCardLegislators.stream().anyMatch(rcl -> rcl.getLegislator().equals(legislator));
    }

    public List<Bill> supportedBills(){
        return reportFactors.stream()
                .filter(f -> f.getVoteSide().equals(VoteSide.Yea))
                .map(ReportFactor::getBill)
                .collect(Collectors.toList());
    }

    public List<Bill> opposedBills(){
        return reportFactors.stream()
                .filter(f -> f.getVoteSide().equals(VoteSide.Nay))
                .map(ReportFactor::getBill)
                .collect(Collectors.toList());
    }

    public List<Legislator> getSelectedLegislators(){
        return reportCardLegislators.stream().map(ReportCardLegislator::getLegislator).collect(Collectors.toList());
    }

    public boolean hasFactors(){
        boolean answer =  reportFactors.size() > 0;
        return answer;
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

    public void resetReportFactorSettings(List<Bill> bills, Map<Long, VoteSide> billRecommendations){
        List<ReportFactor> factorsToRemove = new ArrayList<>();
        for(ReportFactor reportFactor : reportFactors){
            Long billId = reportFactor.getBill().getId();
            if( billRecommendations.containsKey(billId)){
                reportFactor.setVoteSide(billRecommendations.get(billId));
                billRecommendations.remove(billId);
            } else {
                factorsToRemove.add(reportFactor);
            }
        }
        for( ReportFactor reportFactor : factorsToRemove ){
            reportFactors.remove(reportFactor);
        }
        for(Map.Entry<Long, VoteSide> billVotePair : billRecommendations.entrySet()){
            Bill bill = Lists.findfirst(bills, b -> b.getId().equals(billVotePair.getKey()));
            ReportFactor factor = new ReportFactor();
            factor.setVoteSide(billVotePair.getValue());
            factor.setBill(bill);
            addReportFactor(factor);
        }

    }

    public void resetSelectedLegislators(List<Legislator> legislators, List<Long> selectedLegislatorIds){
        List<ReportCardLegislator> toRemove = new ArrayList<>();
        List<Long> newIds = new ArrayList<>(selectedLegislatorIds);
        for( ReportCardLegislator rcl : reportCardLegislators ){
            Long workingId = rcl.getLegislator().getId();
            if ( ! selectedLegislatorIds.contains(workingId) ){
                toRemove.add(rcl);
                newIds.remove(workingId);
            }
            if ( selectedLegislatorIds.contains(workingId) ){
                newIds.remove(workingId);
            }
        }
        reportCardLegislators.removeAll(toRemove);
        for (Long newId : newIds){
            Legislator legislator = Lists.findfirst(legislators, l -> l.getId().equals(newId));
            ReportCardLegislator rcl = new ReportCardLegislator();
            rcl.setLegislator(legislator);
            addReportCardLegislator(rcl);
        }
    }

    public void resetGradeLevels(List<GradeLevel> newGradeLevels){
        GradeLevels newLevels = new GradeLevels(newGradeLevels);
        for( GradeLevel existingLevel : gradeLevelList ){
            long newPercentage = newLevels.getPercentage(existingLevel.getChamber(), existingLevel.getGrade());
            existingLevel.setPercentage(newPercentage);
        }
    }

    public SortedMap<Bill, String> computeFactorSettings(List<Bill> bills){
        List<ReportFactor> factors = getReportFactors();
        Map<Long, ReportFactor> factorsByBillId = Lists.asMap(factors, f -> f.getBill().getId());

        SortedMap<Bill, String> factorSettings = new TreeMap<>();

        for(Bill bill : bills){
            ReportFactor matchingFactor = factorsByBillId.get(bill.getId());
            if ( matchingFactor == null ){
                factorSettings.put(bill, "Unselected");
            } else {
                factorSettings.put(bill, matchingFactor.getVoteSide().getCode());
            }

        }
        return factorSettings;
    }

    public GradeLevels getGradeLevels(){
        return new GradeLevels(gradeLevelList);
    }

}
