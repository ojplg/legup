package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.LookupTable;

import java.util.List;
import java.util.Map;

public class GradeCalculator {

    private final ReportCard reportCard;
    private List<Legislator> legislators;

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

    public LookupTable<Legislator, Bill, Integer> calculate(Map<Bill, List<Vote>> voteRecords){

        LookupTable<Legislator, Bill, Integer> scoreTable = new LookupTable<>();

        for(ReportFactor factor : reportCard.getReportFactors()){

            Bill bill = factor.getBill();
            VoteSide sideRequired = factor.getVoteSide();
            List<Vote> votes = voteRecords.get(bill);

            for(Legislator legislator : legislators) {

                Vote vote = extractVoteForLegislator(votes, legislator);
                if (vote == null) {
                    // ? What to do
                } else if (sideRequired.equals(vote.getVoteSide())) {
                    scoreTable.put(legislator, bill, 1);
                } else if (VoteSide.Nay.equals(sideRequired) && VoteSide.Yea.equals(vote.getVoteSide())
                        || (VoteSide.Yea.equals(sideRequired) && VoteSide.Nay.equals(vote.getVoteSide()))) {
                    scoreTable.put(legislator, bill, -1);
                } else {
                    // what to do?
                }
            }
        }
        return scoreTable;
    }

}

























