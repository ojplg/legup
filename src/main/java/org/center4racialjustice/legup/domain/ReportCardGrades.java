package org.center4racialjustice.legup.domain;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.LookupTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportCardGrades {

    private final ReportCard reportCard;
    private final List<Legislator> legislators;
    private final Map<Bill, List<BillAction>> billActionMap;

    private final LookupTable<Legislator, Bill, Integer> lookupTable;
    private final Map<Legislator, Grade> grades;

    public ReportCardGrades(ReportCard reportCard, List<Legislator> legislators, Map<Bill, List<BillAction>> billActionMap){
        this.reportCard = reportCard;
        this.legislators = legislators;
        this.billActionMap = billActionMap;

        this.lookupTable = calculateLookupTable();
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

    private LookupTable<Legislator, Bill, Integer> calculateLookupTable(){
        GradeCalculator calculator = new GradeCalculator(reportCard, legislators);
        return calculator.calculate(billActionMap);
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
        Bill bill = Lists.findfirst(getBills(), b -> b.getId() == billId);
        List<BillAction> billActions = billActionMap.get(bill);
        return new ReportCardBillAnalysis(bill, billActions, grades);
    }

    public Multimap<Grade, Legislator> collectByGrades(){
        Multimap<Grade, Legislator> collated = MultimapBuilder.hashKeys().arrayListValues().build();
        for( Map.Entry<Legislator, Grade> entry : grades.entrySet()){
            collated.put(entry.getValue(), entry.getKey());
        }
        return collated;
    }

    private Map<Legislator, Integer> sumScores() {
        Map<Legislator, Integer> sums = new HashMap<>();

        for (Legislator legislator : lookupTable.getRowHeadings()) {
            Integer sum = lookupTable.computeRowSummary(legislator, 0, GradeCalculator.ScoreComputer);
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
