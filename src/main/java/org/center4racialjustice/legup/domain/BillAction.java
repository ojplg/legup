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
