package org.center4racialjustice.legup.service;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Data;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.CompletedBillEvent;
import org.center4racialjustice.legup.domain.DisplayAction;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.LegislatorBillAction;
import org.center4racialjustice.legup.domain.LegislatorBillActionType;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.util.Lists;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BillActionCollator {


    private final List<BillAction> allActions;
    private final Multimap<VoteKey,DisplayAction> votes;
    private final List<DisplayAction> sponsorships;
    private final List<DisplayAction> chiefSponsorships;
    private final List<DisplayAction> introductions;
    private final List<String> voteDescriptions;

    private final List<DisplayAction> sponsorRemovals;
    private final List<DisplayAction> chiefSponsorRemovals;

    public BillActionCollator(List<BillAction> actions){
        this(actions, null);
    }

    public BillActionCollator(List<BillAction> actions, Legislator legislator){
        allActions = actions;
        ImmutableMultimap.Builder<VoteKey,DisplayAction> votesBldr = new ImmutableMultimap.Builder<>();
        List<DisplayAction> sponsorships = new ArrayList<>();
        List<DisplayAction> chiefSponsorships = new ArrayList<>();
        List<DisplayAction> introductions = new ArrayList<>();

        List<DisplayAction> sponsorRemovals = new ArrayList<>();
        List<DisplayAction> chiefSponsorRemovals = new ArrayList<>();


        List<String> voteDescriptionSet = new ArrayList<>();

        for(BillAction action : actions){
            if( action.isVote() ){
                voteDescriptionSet.add(action.getRawActionData());
            }
            for( LegislatorBillAction legislatorBillAction : action.getLegislatorBillActions()) {
                if( legislator != null ){
                    if ( ! legislator.equals(legislatorBillAction.getLegislator()) ){
                        continue;
                    }
                }
                DisplayAction displayAction = new DisplayAction(action, legislatorBillAction);
                switch (legislatorBillAction.getLegislatorBillActionType().getCode()) {
                    case LegislatorBillActionType.VoteCode:
                        votesBldr.put(new VoteKey(displayAction), displayAction);
                        break;
                    case LegislatorBillActionType.SponsorCode:
                        sponsorships.add(displayAction);
                        break;
                    case LegislatorBillActionType.ChiefSponsorCode:
                        chiefSponsorships.add(displayAction);
                        break;
                    case LegislatorBillActionType.IntroduceCode:
                        introductions.add(displayAction);
                        break;
                    case LegislatorBillActionType.RemoveSponsorCode:
                        sponsorRemovals.add(displayAction);
                        break;
                    case LegislatorBillActionType.RemoveChiefSponsorCode:
                        chiefSponsorRemovals.add(displayAction);
                        break;
                    default:
                        throw new RuntimeException("Unknown bill action type " + action.getBillActionType());
                }
            }
        }

        sponsorships.sort(DisplayAction.ByBillComparator);
        chiefSponsorships.sort(DisplayAction.ByBillComparator);

        this.votes = votesBldr.build();
        this.sponsorships = Collections.unmodifiableList(sponsorships);
        this.chiefSponsorships = Collections.unmodifiableList(chiefSponsorships);
        this.voteDescriptions = Collections.unmodifiableList(voteDescriptionSet);

        this.sponsorRemovals = Collections.unmodifiableList(sponsorRemovals);
        this.chiefSponsorRemovals = Collections.unmodifiableList(chiefSponsorRemovals);
        this.introductions = Collections.unmodifiableList(introductions);
    }

    public List<DisplayAction> getSponsorships() {
        return sponsorships;
    }

    public List<DisplayAction> getChiefSponsorships() {
        return chiefSponsorships;
    }

    public List<DisplayAction> getIntroductions() {
        return introductions;
    }

    public List<Legislator> getSponsors(Chamber chamber){
        return generateSponsorList(chamber, false);
    }

    public List<Legislator> getChiefSponsors(Chamber chamber){
        return generateSponsorList(chamber, true);
    }

    public List<Legislator> getIntroductions(Chamber chamber){
        return  getIntroductions().stream()
                .map(DisplayAction::getLegislator)
                .filter(leg -> leg.getChamber().equals(chamber))
                .collect(Collectors.toList());
    }

    private List<Legislator> generateSponsorList(Chamber chamber, boolean chief){
        BillActionType add;
        BillActionType remove;
        if( chief ){
            add = BillActionType.CHIEF_SPONSOR;
            remove = BillActionType.REMOVE_CHIEF_SPONSOR;
        } else {
            add = BillActionType.SPONSOR;
            remove = BillActionType.REMOVE_SPONSOR;
        }

        List<Legislator> sponsorList = new ArrayList<>();
        List<BillAction> orderedActions = new ArrayList<>();
        orderedActions.addAll(allActions);
        Collections.sort(orderedActions);
        for(BillAction action : allActions ){
            if( action.getBillActionType().equals(add) ){
                Legislator legislator = action.getSingleLegislator();
                sponsorList.add(legislator);
            }
            if ( action.getBillActionType().equals(remove) ){
                Legislator legislator = action.getSingleLegislator();
                sponsorList.remove(legislator);
            }
        }
        return sponsorList;
    }

    public Collection<DisplayAction> getVotes(String voteDescription, Chamber chamber, VoteSide voteSide){
        VoteKey voteKey = new VoteKey(voteDescription, chamber, voteSide);
        Collection<DisplayAction> particularVotes = votes.get(voteKey);
        return particularVotes;
    }

    public List<String> getVoteDescriptions(){
        return voteDescriptions;
    }

    public BillAction getActionFromRawData(String rawActionData){
        return Lists.findfirst(allActions, action -> action.getRawActionData().equals(rawActionData));
    }

    public BillAction getMatchingAction(CompletedBillEvent billEvent){
        return Lists.findfirst(allActions, action -> action.matchesEvent(billEvent));
    }

    public Multimap<LocalDate,BillAction> getActionsByDate(){
        Multimap<LocalDate,BillAction> map = MultimapBuilder.treeKeys().arrayListValues().build();
        allActions.forEach(action -> map.put(action.getActionDateAsLocalDate(), action));
        return map;
    }

    public List<DisplayAction> getVotes(){
        ArrayList<DisplayAction> voteList = new ArrayList<>( votes.values());
        Collections.sort(voteList, DisplayAction.ByBillComparator);
        return voteList;
    }

    @Data
    private class VoteKey {
        private final String description;
        private final Chamber chamber;
        private final VoteSide voteSide;

        VoteKey(String description, Chamber chamber, VoteSide voteSide){
            this.description = description;
            this.chamber = chamber;
            this.voteSide = voteSide;
        }

        VoteKey(DisplayAction displayAction){
            this.description = displayAction.getRawActionData();
            this.chamber = displayAction.getActionChamber();
            this.voteSide = displayAction.getVoteSide();
        }
    }

}
