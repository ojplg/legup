package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.LookupTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class GradeCalculator {

    private final ReportCard reportCard;
    private List<Legislator> legislators;

    public GradeCalculator(ReportCard reportCard, List<Legislator> legislators){
        this.reportCard = reportCard;
        this.legislators = legislators;
    }

    private static int add(int addend1, int addend2){
        return addend1 + addend2;
    }

    public LookupTable<Legislator, Bill, Integer> calculate(Map<Bill, List<BillAction>> actionMap){

        LookupTable<Legislator, Bill, Integer> scoreTable = new LookupTable<>();

        for(ReportFactor factor : reportCard.getReportFactors()){

            Bill bill = factor.getBill();
            VoteSide sideRequired = factor.getVoteSide();
            List<BillAction> actions = actionMap.get(bill);

            for(Legislator legislator : legislators) {
                List<BillAction> legislatorActions = actions.stream()
                        .filter(a -> legislator.equals(a.getLegislator()))
                        .collect(Collectors.toList());

                for(BillAction legislatorAction : legislatorActions) {
                    if( legislatorAction.isVote()) {
                        Vote vote = legislatorAction.asVote();
                        if (vote == null) {
                            // ? What to do
                        } else if (sideRequired.equals(vote.getVoteSide())) {
                            scoreTable.merge(legislator, bill, 1, GradeCalculator::add );
                        } else if (VoteSide.Nay.equals(sideRequired) && VoteSide.Yea.equals(vote.getVoteSide())
                                || (VoteSide.Yea.equals(sideRequired) && VoteSide.Nay.equals(vote.getVoteSide()))) {
                            scoreTable.merge(legislator, bill, -1 , GradeCalculator::add );
                        } else {
                            // what to do?
                        }
                    } else if (legislatorAction.getBillActionType().equals(BillActionType.SPONSOR)){
                        if( sideRequired.equals(VoteSide.Yea)){
                            scoreTable.merge(legislator, bill, 2, GradeCalculator::add);
                        } else {
                            scoreTable.merge(legislator, bill, -2, GradeCalculator::add);
                        }
                    } else if (legislatorAction.getBillActionType().equals(BillActionType.CHIEF_SPONSOR)){
                        if( sideRequired.equals(VoteSide.Yea)){
                            scoreTable.merge(legislator, bill, 3, GradeCalculator::add);
                        } else {
                            scoreTable.merge(legislator, bill, -3, GradeCalculator::add);
                        }
                    }
                }
            }
        }
        return scoreTable;
    }

    public static Map<Legislator, String> assignGrades(LookupTable<Legislator, Bill, Integer> scores){
        Map<Legislator, Integer> sums = sumScores(scores);
        Integer min = findMin(sums);
        Integer max = findMax(sums);
        Integer spread = max - min;
        Integer qunitile = spread / 5;

        Map<Legislator, String> grades = new HashMap<>();

        for(Map.Entry<Legislator, Integer> entry : sums.entrySet()){
            if( entry.getValue() > min + 4 * qunitile ){
                grades.put(entry.getKey(), "A");
            } else if( entry.getValue() > min + 3 * qunitile ){
                grades.put(entry.getKey(), "B");
            } else if( entry.getValue() > min + 2 * qunitile ){
                grades.put(entry.getKey(), "C");
            } else if( entry.getValue() > min + 1 * qunitile ){
                grades.put(entry.getKey(), "D");
            } else {
                grades.put(entry.getKey(), "F");
            }
        }

        return grades;
    }

    private static Map<Legislator, Integer> sumScores(LookupTable<Legislator, Bill, Integer> scores){
        Map<Legislator, Integer> sums = new HashMap<>();

        for(Legislator legislator : scores.getRowHeadings()){
            BinaryOperator<Integer> scoreComputer = (i, j) -> i + j;
            Integer sum = scores.computeRowSummary(legislator, 0, scoreComputer);
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


