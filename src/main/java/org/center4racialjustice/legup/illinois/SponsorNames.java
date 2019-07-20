package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public void completeAll(List<Legislator> legislators){
        Map<String, Legislator> legislatorMap = Lists.asMap(legislators, Legislator::getMemberId);
        if( chiefHouseSponsor != null && legislatorMap.containsKey(chiefHouseSponsor.getMemberId())){
            chiefHouseSponsor.complete(legislatorMap.get(chiefHouseSponsor.getMemberId()));
        }
        if( chiefSenateSponsor != null && legislatorMap.containsKey(chiefSenateSponsor.getMemberId())){
            chiefSenateSponsor.complete(legislatorMap.get(chiefSenateSponsor.getMemberId()));
        }
        for( SponsorName sponsorName : houseSponsors ){
            if( legislatorMap.containsKey(sponsorName.getMemberId())){
                sponsorName.complete(legislatorMap.get(sponsorName.getMemberId()));
            }
        }
        for( SponsorName sponsorName : senateSponsors ){
            if( legislatorMap.containsKey(sponsorName.getMemberId())){
                sponsorName.complete(legislatorMap.get(sponsorName.getMemberId()));
            }
        }
    }

    public SponsorName findMatchingSponsor(Name name){
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
        return Lists.findfirst(senateSponsors, sponsor -> sponsor.matchesLegislatorName(name));
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
