package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.LookupTable;

import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class GradeCalculator {

    public static BinaryOperator<Integer> ScoreComputer = (i, j) -> i + j;

    private static final int VOTE_SCORE = 1;
    private static final int SPONSOR_SCORE = 2;
    private static final int CHIEF_SPONSOR_SCORE = 3;

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
                    } else if (legislatorAction.isSponsorship()){
                        if( sideRequired.equals(VoteSide.Yea)){
                            scoreTable.merge(legislator, bill, 2, GradeCalculator::add);
                        } else {
                            scoreTable.merge(legislator, bill, -2, GradeCalculator::add);
                        }
                    } else if (legislatorAction.isChiefSponsorship()){
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

}
