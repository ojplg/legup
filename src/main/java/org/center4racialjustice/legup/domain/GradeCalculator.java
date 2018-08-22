package org.center4racialjustice.legup.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeCalculator {

    private final ReportCard reportCard;
    private List<Legislator> legislators;

    /*

    need bills
    need votes

     */

    public GradeCalculator(ReportCard reportCard, List<Legislator> legislators){
        this.reportCard = reportCard;
        this.legislators = legislators;
    }

    public List<Long> extractBillIds(){
        return null;
    }

    private Vote extractVoteForLegislator(List<Vote> votes, Legislator legislator){
        for( Vote vote : votes ){
            if ( vote.getLegislator().equals(legislator)){
                return vote;
            }
        }
        return null;
    }

    public Map<Legislator, Integer> calculate(Map<Bill, List<Vote>> voteRecords){

        Map<Legislator, Integer> scores = new HashMap<>();

        for(ReportFactor factor : reportCard.getReportFactors()){

            Bill bill = factor.getBill();
            VoteSide sideRequired = factor.getVoteSide();
            List<Vote> votes = voteRecords.get(bill);

            for(Legislator legislator : legislators) {
                final int score;

                Vote vote = extractVoteForLegislator(votes, legislator);
                if (vote == null) {
                    score = 0;
                    // ? What to do
                } else if (sideRequired.equals(vote.getVoteSide())) {
                    score = 1;
                } else if (VoteSide.Nay.equals(sideRequired) && VoteSide.Yea.equals(vote.getVoteSide())
                        || (VoteSide.Yea.equals(sideRequired) && VoteSide.Nay.equals(vote.getVoteSide()))) {
                    score = -1;
                } else {
                    // what to do?
                    score = 0;
                }

                scores.compute(legislator,
                        (l, s) -> {
                            if (s == null) {
                                return score;
                            } else {
                                return score + s;
                            }
                        });
            }
        }
        return scores;
    }

}


























