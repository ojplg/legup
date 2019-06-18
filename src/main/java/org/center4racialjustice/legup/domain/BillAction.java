package org.center4racialjustice.legup.domain;

import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BillAction {

    public static final Comparator<BillAction> ByLegislatorComparator = Comparator.comparing(BillAction::getLegislator);
    public static final Comparator<BillAction> ByBillComparator = Comparator.comparing(BillAction::getBill);

    private Long id;
    private Bill bill;
    private Legislator legislator;
    private BillActionType billActionType;
    private String billActionDetail;
    private BillActionLoad billActionLoad;

    public boolean isVote(){
        return BillActionType.VOTE.equals(billActionType);
    }

    public boolean isSponsorship(){
        return BillActionType.SPONSOR.equals(billActionType);
    }

    public boolean isChiefSponsorship(){
        return BillActionType.CHIEF_SPONSOR.equals(billActionType);
    }

    public int score(VoteSide preferredSide){
        int scoreValue = BillActionType.scoreValue(billActionType);
        if( BillActionType.VOTE.equals(billActionType)){
            VoteSide recordedSide = VoteSide.fromCode(billActionDetail);
            if( recordedSide == VoteSide.NotVoting || recordedSide == VoteSide.Present ){
                return 0;
            } else {
                return preferredSide == recordedSide ? scoreValue : -scoreValue;
            }
        } else if (BillActionType.SPONSOR.equals(billActionType)
                || BillActionType.CHIEF_SPONSOR.equals(billActionType)){
            return VoteSide.Yea.equals(preferredSide) ? scoreValue : -scoreValue;
        }
        throw new RuntimeException("No way to score " + billActionType);
    }

    public static BillAction fromVote(Vote vote){
        BillAction billAction = new BillAction();
        billAction.setId(vote.getId());
        billAction.setBill(vote.getBill());
        billAction.setLegislator(vote.getLegislator());
        billAction.setBillActionType(BillActionType.VOTE);
        billAction.setBillActionDetail(vote.getVoteSide().getCode());
        billAction.setBillActionLoad(vote.getBillActionLoad());
        return billAction;
    }

    public Vote asVote(){
        if ( ! isVote() ){
            throw new RuntimeException("This action is not a vote: " + this.toString());
        }
        Vote vote = new Vote();
        vote.setId(id);
        vote.setBill(bill);
        vote.setLegislator(legislator);
        vote.setVoteSide(VoteSide.fromCode(billActionDetail));
        vote.setBillActionLoad(billActionLoad);
        return vote;
    }

    public static List<Vote> filterAndConvertToVotes(List<BillAction> actions){
        return actions.stream()
                .filter(BillAction::isVote)
                .map(BillAction::asVote)
                .collect(Collectors.toList());
    }
}
