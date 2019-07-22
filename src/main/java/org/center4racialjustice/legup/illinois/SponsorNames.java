package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.domain.CompletedBillEvent;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.service.LegislativeStructure;
import org.center4racialjustice.legup.util.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class SponsorNames {

    private SponsorName chiefHouseSponsor;
    private SponsorName chiefSenateSponsor;

    private List<SponsorName> houseSponsors;
    private List<SponsorName> senateSponsors;

    public SponsorNames(List<SponsorName> houseNames, List<SponsorName> senateNames){
        if( houseNames.size() > 0 ){
            chiefHouseSponsor = houseNames.get(0);
        }
        if ( houseNames.size() > 1 ){
            houseSponsors = new ArrayList<>(houseNames.subList(1, houseNames.size()));
        } else {
            houseSponsors = Collections.emptyList();
        }
        if( senateNames.size() > 0 ){
            chiefSenateSponsor = senateNames.get(0);
        }
        if ( senateNames.size() > 1 ){
            senateSponsors = new ArrayList<>(senateNames.subList(1, senateNames.size()));
        } else {
            senateSponsors = Collections.emptyList();
        }
    }

    public int totalSponsorCount(){
        int sum = 0;
        if (chiefHouseSponsor != null){
            sum++;
        }
        if ( chiefSenateSponsor != null){
            sum++;
        }
        return sum + houseSponsors.size() + senateSponsors.size();
    }

    private void completeOne(SponsorName sponsorName, LegislativeStructure legislativeStructure){
        if( sponsorName != null ){
            Legislator legislator = legislativeStructure.findLegislatorByMemberID(sponsorName.getMemberId());
            sponsorName.complete(legislator);
        }
    }

    public void completeAll(LegislativeStructure legislativeStructure){
        completeOne(chiefHouseSponsor, legislativeStructure);
        completeOne(chiefSenateSponsor, legislativeStructure);
        houseSponsors.forEach(
                sponsorName -> completeOne(sponsorName, legislativeStructure)
        );
        senateSponsors.forEach(
                sponsorName -> completeOne(sponsorName, legislativeStructure)
        );
    }

    private List<SponsorName> allSponsorNames(){
        List<SponsorName> allNames = new ArrayList<>();
        allNames.addAll(houseSponsors);
        allNames.addAll(senateSponsors);
        if( chiefHouseSponsor != null ){
            allNames.add(chiefHouseSponsor);
        }
        if( chiefSenateSponsor != null) {
            allNames.add(chiefSenateSponsor);
        }
        return allNames;
    }

    public List<String> findSponsorshipMismatches(List<CompletedBillEvent> events){
        List<Legislator> sponsors = Lists.mapWithoutNulls(allSponsorNames(), SponsorName::getLegislator);
        List<String> errors = new ArrayList<>();
        List<Legislator> eventLegislators = new ArrayList<>();
        for(CompletedBillEvent event : events){
            Legislator legislator = event.getLegislator();
            if( legislator != null ){
                eventLegislators.add(legislator);
                if( ! sponsors.contains(legislator)) {
                    errors.add("Could not find event for sponsor " + legislator);
                }
            }
        }
        for(Legislator legislator : eventLegislators){
            if( ! sponsors.contains(legislator)){
                errors.add("Incorrect sponsor event " + legislator);
            }
        }
        return errors;
    }

    public SponsorName findMatchingSponsor(String memberID, Name name){
        // first try to match by name, then try member id.
        // TODO: Think about this. Maybe try both? Maybe something different?
        if( chiefSenateSponsor.matchesLegislatorName(name) ){
            return chiefSenateSponsor;
        }
        if( chiefHouseSponsor.matchesLegislatorName(name)){
            return chiefHouseSponsor;
        }
        SponsorName sponsorName = Lists.findfirst(houseSponsors, sponsor -> sponsor.matchesLegislatorName(name));
        if ( sponsorName != null ){
            return sponsorName;
        }
        sponsorName = Lists.findfirst(senateSponsors, sponsor -> sponsor.matchesLegislatorName(name));
        if ( sponsorName != null ){
            return sponsorName;
        }

        if( chiefSenateSponsor.matchesMemberID(memberID) ){
            return chiefSenateSponsor;
        }
        if( chiefHouseSponsor.matchesMemberID(memberID)){
            return chiefHouseSponsor;
        }
        sponsorName = Lists.findfirst(houseSponsors, sponsor -> sponsor.matchesMemberID(memberID));
        if ( sponsorName != null ){
            return sponsorName;
        }
        return Lists.findfirst(senateSponsors, sponsor -> sponsor.matchesMemberID(memberID));
    }

    public List<SponsorName> getUncollated(){
        List<SponsorName> incompletes = new ArrayList<>();

        if( chiefHouseSponsor != null &&
                ! chiefHouseSponsor.isComplete() ){
            incompletes.add(chiefHouseSponsor);
        }
        if( chiefSenateSponsor != null &&
                ! chiefSenateSponsor.isComplete() ){
            incompletes.add(chiefSenateSponsor);
        }
        for(SponsorName sponsorName : houseSponsors){
            if (!sponsorName.isComplete()){
                incompletes.add(sponsorName);
            }
        }
        for(SponsorName sponsorName : senateSponsors){
            if (!sponsorName.isComplete()){
                incompletes.add(sponsorName);
            }
        }
        return incompletes;
    }

    public List<String> getAllRawNames(){
        List<String> rawNames = new ArrayList<>();
        if( chiefHouseSponsor != null ){
            rawNames.add(chiefHouseSponsor.getRawName());
        }
        if( chiefSenateSponsor != null ){
            rawNames.add(chiefSenateSponsor.getRawName());
        }
        for(SponsorName houseName : houseSponsors){
            rawNames.add(houseName.getRawName());
        }
        for(SponsorName senateName : senateSponsors){
            rawNames.add(senateName.getRawName());
        }
        return rawNames;
    }
}
