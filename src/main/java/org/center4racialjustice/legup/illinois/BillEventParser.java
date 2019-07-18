package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.BillEventInterpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillEventParser implements BillEventInterpreter {

    private static final Pattern FiledWithClerkPattern =
            Pattern.compile("Filed with (?:the Clerk|Secretary) by (?:Sen|Rep). (.*)");

    private static final Pattern AddedChiefSponsorPattern =
            Pattern.compile("Added (?:as )?Chief Co-Sponsor (?:Sen|Rep). (.*)");

    private static final Pattern AddedSponsorPattern =
            Pattern.compile("Added (?:as )?Co-Sponsor (?:Sen|Rep). (.*)");

    private static final Pattern ChiefSenateSponsorPattern =
            Pattern.compile("Chief (?:House|Senate) Sponsor (?:Sen|Rep). (.*)");

    private static final Pattern AddedAlternateCoSponsorPattern =
            Pattern.compile("Added (?:as )?Alternate Co-Sponsor (?:Sen|Rep). (.*)");

    private static final Pattern AddedAlternateChiefCoSponsorPattern =
            Pattern.compile("Added (?:as )?Alternate Chief Co-Sponsor (?:Sen|Rep). (.*)");

    private static final Pattern CommitteeReferralPattern =
            Pattern.compile("Referred to ([\\w\\s-]+)");

    private static final Pattern CommitteeAssignmentPattern =
            Pattern.compile("Assigned to ([\\w\\s-]+)");

    private static final Pattern CommitteePostponementPattern =
            Pattern.compile("Postponed - ([\\w\\s-]+)");

    private static final Pattern CommitteeAmendmentPattern =
            Pattern.compile("(?:Senate|House) Committee Amendment No. \\d+ Filed with (?:Clerk|Secretary) by (?:Sen|Rep). (.*)");

    private static final Pattern CommitteeVotePattern =
            Pattern.compile("(?:Do Pass as Amended|Recommends Do Pass|Reported Back To|Do Pass / Short Debate)? ([\\w\\s-/]+); \\d\\d\\d-\\d\\d\\d-\\d\\d\\d$");

    private static final Pattern VotePattern =
            Pattern.compile(".*\\d\\d\\d-\\d\\d\\d-\\d\\d\\d$");

    private static final Map<Pattern, BiFunction<BillEvent, String, BillEventData>> NameGrabbingPatterns;

    private static final Map<Pattern, Function<BillEvent, BillEventData>> NoGrabPatterns;

    static {
        NameGrabbingPatterns = new HashMap<>();
        NameGrabbingPatterns.put(
                FiledWithClerkPattern,
                ChiefSponsorshipBillEvent::new);
        NameGrabbingPatterns.put(
                AddedChiefSponsorPattern,
                ChiefSponsorshipBillEvent::new);
        NameGrabbingPatterns.put(
                AddedSponsorPattern,
                SponsorshipBillEvent::new);
        NameGrabbingPatterns.put(
                ChiefSenateSponsorPattern,
                ChiefSponsorshipBillEvent::new);
        NameGrabbingPatterns.put(
                AddedAlternateCoSponsorPattern,
                SponsorshipBillEvent::new);
        NameGrabbingPatterns.put(
                AddedAlternateChiefCoSponsorPattern,
                ChiefSponsorshipBillEvent::new);
        NameGrabbingPatterns.put(
                CommitteeReferralPattern,
                CommitteeBillEvent::referral);
        NameGrabbingPatterns.put(
                CommitteeAssignmentPattern,
                CommitteeBillEvent::assignment);
        NameGrabbingPatterns.put(
                CommitteePostponementPattern,
                CommitteeBillEvent::postponement);
        NameGrabbingPatterns.put(
                CommitteeAmendmentPattern,
                CommitteeAmendmentFiledBillEvent::new);
        NameGrabbingPatterns.put(
                CommitteeVotePattern,
                CommitteeBillEvent::vote);

        NoGrabPatterns = new HashMap<>();
        NoGrabPatterns.put(
                VotePattern,
                VoteBillEvent::new);
    }

    @Override
    public BillEventData parse(BillEvent billEvent) {
        String rawContents = billEvent.getRawContents();

        for(Map.Entry<Pattern, BiFunction<BillEvent,String, BillEventData>> parserEntry : NameGrabbingPatterns.entrySet()){
            Matcher matcher = parserEntry.getKey().matcher(rawContents);
            if( matcher.matches() ){
                String grabbed = matcher.group(1);
                return parserEntry.getValue().apply(billEvent, grabbed);
            }
        }

        for(Map.Entry<Pattern, Function<BillEvent, BillEventData>> parserEntry : NoGrabPatterns.entrySet()){
            Matcher matcher = parserEntry.getKey().matcher(rawContents);
            if( matcher.matches() ){
                return parserEntry.getValue().apply(billEvent);
            }
        }

        return new UnclassifiedEventData(billEvent);
    }
}
