package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.BillEventKey;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.util.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BillSearchResults {

    private static final Logger log = LogManager.getLogger(BillSearchResults.class);

    public enum MatchStatus {
        Unchecked,NoPriorValues,MatchedValues,UnmatchedValues;
    }

    private final Bill parsedBill;
    private final SponsorNames sponsorNames;
    private final long checksum;
    private final String url;
    private final List<BillVotesResults> votesResults;
    private final List<BillEventData> billEvents;

    public BillSearchResults(BillHtmlParser billHtmlParser,
                             List<Legislator> legislators,
                             List<BillVotesResults> votesResults,
                             List<BillEventData> billEvents){
        this.parsedBill = billHtmlParser.getBill();
        this.sponsorNames = billHtmlParser.getSponsorNames();
        this.sponsorNames.completeAll(legislators);
        this.checksum = billHtmlParser.getChecksum();
        this.url = billHtmlParser.getUrl();
        this.votesResults = votesResults;
        this.billEvents = billEvents;
    }

    public BillIdentity getBillIdentity(){
        return parsedBill.getBillIdentity();
    }

    public List<BillEventData> getBillEvents(){
        return billEvents;
    }

    public List<BillEventKey> generateSearchedVoteLoadKeys(){
        return Collections.emptyList();
    }

//    public BillVotesResults getSearchedResults(BillEventKey key){
//        log.info("Searching for bill event key " + key);
//        List<BillEventKey> possibleKeys = Lists.map(votesResults, BillVotesResults::generateEventKey);
//        possibleKeys.forEach(pk -> log.info("Possible Key: " + pk));
//
//        return Lists.findfirst(votesResults, res -> res.generateEventKey().equals(key));
//    }

    public Bill getParsedBill(){
        return parsedBill;
    }

    public SponsorNames getSponsorNames() {
        return sponsorNames;
    }

    public long getChecksum() {
        return checksum;
    }

    public String getUrl(){
        return url;
    }

//    public BillActionLoad createVoteBillActionLoad(Bill bill, BillEventKey key){
//        BillVotesResults results = getSearchedResults(key);
//        return BillActionLoad.create(bill, results.getUrl(), results.getChecksum());
//    }

    public SponsorName getSponsorName(BillEventData billEventData){
        String rawName = billEventData.getRawLegislatorName();
        SponsorName sponsorName = sponsorNames.findMatchingSponsor(rawName);
        return sponsorName;
    }

    public BillVotesResults getBillVotesResults(BillEventData billEventData){
        log.info("Searching for " + billEventData);
        votesResults.forEach(vr -> log.info("possible match " + vr));
        return Lists.findfirst(votesResults, vr -> vr.matches(billEventData));
    }

    public List<Name> getUncollatedVotes(){
        List<Name> uncollatedVotes = new ArrayList<>();
        for(BillVotesResults results : votesResults){
            uncollatedVotes.addAll(results.getUncollatedNames());
        }
        return uncollatedVotes;
    }

    public List<SponsorName> getUncollatedSponsors(){
        return sponsorNames.getUncollated();
    }

}
