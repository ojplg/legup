package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.CompletedBillEvent;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.service.LegislativeStructure;
import org.center4racialjustice.legup.util.Lists;

import java.util.ArrayList;
import java.util.List;

@Data
public class SponsorNames {

    private static final Logger log = LogManager.getLogger(SponsorNames.class);

    private List<SponsorName> houseSponsors;
    private List<SponsorName> senateSponsors;

    public SponsorNames(List<SponsorName> houseNames, List<SponsorName> senateNames){
        houseSponsors = new ArrayList<>(houseNames);
        senateSponsors = new ArrayList<>(senateNames);
    }

    public int totalSponsorCount(){
        return  houseSponsors.size() + senateSponsors.size();
    }

    private void completeOne(SponsorName sponsorName, LegislativeStructure legislativeStructure){
        if( sponsorName != null ){
            Legislator legislator = legislativeStructure.findLegislatorByMemberID(sponsorName.getMemberId());
            if( legislator == null ){
                log.warn("Cannot match " + sponsorName);
            }
            sponsorName.complete(legislator);
        }
    }

    public void completeAll(LegislativeStructure legislativeStructure){
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
        return allNames;
    }

    public List<String> findSponsorshipMismatches(List<CompletedBillEvent> sponsorEvents, List<CompletedBillEvent> removalEvents){
        List<Legislator> sponsors = Lists.mapWithoutNulls(allSponsorNames(), SponsorName::getLegislator);
        List<String> errors = new ArrayList<>();
        List<Legislator> eventLegislators = new ArrayList<>();
        List<Legislator> removedLegislators = Lists.map(removalEvents, CompletedBillEvent::getLegislator);
        for(CompletedBillEvent event : sponsorEvents){
            Legislator legislator = event.getLegislator();
            if( legislator != null ){
                eventLegislators.add(legislator);
                if( ! sponsors.contains(legislator) && ! removedLegislators.contains(legislator)) {
                    errors.add("Could not find event for sponsor " + legislator);
                }
            }
        }
        for(Legislator sponsor : sponsors){
            if( ! eventLegislators.contains(sponsor)){
                errors.add("Incorrect sponsor event " + sponsor);
            }
        }
        return errors;
    }

    public SponsorName findMatchingSponsor(String memberID, Name name){
        // first try to match by name, then try member id.
        // TODO: Think about this. Maybe try both? Maybe something different?
        SponsorName sponsorName = Lists.findfirst(houseSponsors, sponsor -> sponsor.matchesLegislatorName(name));
        if ( sponsorName != null ){
            return sponsorName;
        }
        sponsorName = Lists.findfirst(senateSponsors, sponsor -> sponsor.matchesLegislatorName(name));
        if ( sponsorName != null ){
            return sponsorName;
        }

        sponsorName = Lists.findfirst(houseSponsors, sponsor -> sponsor.matchesMemberID(memberID));
        if ( sponsorName != null ){
            return sponsorName;
        }
        return Lists.findfirst(senateSponsors, sponsor -> sponsor.matchesMemberID(memberID));
    }

    public List<SponsorName> getUncollated(){
        List<SponsorName> incompletes = new ArrayList<>();

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
        for(SponsorName houseName : houseSponsors){
            rawNames.add(houseName.getRawName());
        }
        for(SponsorName senateName : senateSponsors){
            rawNames.add(senateName.getRawName());
        }
        return rawNames;
    }
}
