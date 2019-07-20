package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.BillEventInterpreter;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;

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

    private final Map<Pattern, BiFunction<BillEvent, String, BillEventData>> nameGrabbingPatterns;
    private final Map<Pattern, Function<BillEvent, BillEventData>> noGrabPatterns;
    private final NameParser nameParser;

    public BillEventParser() {
        this(new NameParser());
    }

    public BillEventParser(NameParser nameParser) {
        this.nameParser = nameParser;

        nameGrabbingPatterns = new HashMap<>();
        nameGrabbingPatterns.put(
                FiledWithClerkPattern,
                this::newChiefSponsorshipEvent);
        nameGrabbingPatterns.put(
                AddedChiefSponsorPattern,
                this::newChiefSponsorshipEvent);
        nameGrabbingPatterns.put(
                AddedSponsorPattern,
                this::newSponsorshipEvent);
        nameGrabbingPatterns.put(
                ChiefSenateSponsorPattern,
                this::newChiefSponsorshipEvent);
        nameGrabbingPatterns.put(
                AddedAlternateCoSponsorPattern,
                this::newSponsorshipEvent);
        nameGrabbingPatterns.put(
                AddedAlternateChiefCoSponsorPattern,
                this::newChiefSponsorshipEvent);
        nameGrabbingPatterns.put(
                CommitteeReferralPattern,
                CommitteeBillEvent::referral);
        nameGrabbingPatterns.put(
                CommitteeAssignmentPattern,
                CommitteeBillEvent::assignment);
        nameGrabbingPatterns.put(
                CommitteePostponementPattern,
                CommitteeBillEvent::postponement);
        nameGrabbingPatterns.put(
                CommitteeAmendmentPattern,
                CommitteeAmendmentFiledBillEvent::new);
        nameGrabbingPatterns.put(
                CommitteeVotePattern,
                CommitteeVoteEvent::new);

        noGrabPatterns = new HashMap<>();
        noGrabPatterns.put(
                VotePattern,
                VoteBillEvent::new);
    }

    private ChiefSponsorshipBillEvent newChiefSponsorshipEvent(BillEvent billEvent, String rawName){
        Name name = nameParser.fromRegularOrderString(rawName);
        return new ChiefSponsorshipBillEvent(billEvent, rawName, name);
    }

    private SponsorshipBillEvent newSponsorshipEvent(BillEvent billEvent, String rawName){
        Name name = nameParser.fromRegularOrderString(rawName);
        return new SponsorshipBillEvent(billEvent, rawName, name);
    }

    @Override
    public BillEventData parse(BillEvent billEvent) {
        String rawContents = billEvent.getRawContents();

        for(Map.Entry<Pattern, BiFunction<BillEvent,String, BillEventData>> parserEntry : nameGrabbingPatterns.entrySet()){
            Matcher matcher = parserEntry.getKey().matcher(rawContents);
            if( matcher.matches() ){
                String grabbed = matcher.group(1);
                return parserEntry.getValue().apply(billEvent, grabbed);
            }
        }

        for(Map.Entry<Pattern, Function<BillEvent, BillEventData>> parserEntry : noGrabPatterns.entrySet()){
            Matcher matcher = parserEntry.getKey().matcher(rawContents);
            if( matcher.matches() ){
                return parserEntry.getValue().apply(billEvent);
            }
        }

        return new UnclassifiedEventData(billEvent);
    }
}
