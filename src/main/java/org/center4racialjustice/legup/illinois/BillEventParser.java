package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.BillEventInterpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
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
            Pattern.compile("Referred to ([\\w\\s]+)");

    private static final Pattern CommitteeAssignmentPattern =
            Pattern.compile("Assigned to ([\\w\\s]+)");

    private static final Pattern CommitteePostponementPattern =
            Pattern.compile("Postponed - ([\\w\\s]+)");

    private static final Pattern CommitteeAmendmentPattern =
            Pattern.compile("(?:Senate|House) Committee Amendment No. \\d+ Filed with (?:Clerk|Secretary) by (?:Sen|Rep). (.*)");

    private static final Map<Pattern, BiFunction<String, String, BillEventData>> NameGrabbingPatterns;

    static {
        NameGrabbingPatterns = new HashMap<>();
        NameGrabbingPatterns.put(
                FiledWithClerkPattern,
                (raw, grab) -> new ChiefSponsorshipBillEvent(raw, grab));
        NameGrabbingPatterns.put(
                AddedChiefSponsorPattern,
                (raw, grab) -> new ChiefSponsorshipBillEvent(raw, grab));
        NameGrabbingPatterns.put(
                AddedSponsorPattern,
                (raw, grab) -> new SponsorshipBillEvent(raw, grab));
        NameGrabbingPatterns.put(
                ChiefSenateSponsorPattern,
                (raw, grab) -> new ChiefSponsorshipBillEvent(raw, grab));
        NameGrabbingPatterns.put(
                AddedAlternateCoSponsorPattern,
                (raw, grab) -> new SponsorshipBillEvent(raw, grab));
        NameGrabbingPatterns.put(
                AddedAlternateChiefCoSponsorPattern,
                (raw, grab) -> new ChiefSponsorshipBillEvent(raw, grab));
        NameGrabbingPatterns.put(
                CommitteeReferralPattern,
                (raw, grab) -> CommitteeBillEvent.referral(raw, grab));
        NameGrabbingPatterns.put(
                CommitteeAssignmentPattern,
                (raw, grab) -> CommitteeBillEvent.assignment(raw, grab));
        NameGrabbingPatterns.put(
                CommitteePostponementPattern,
                (raw, grab) -> CommitteeBillEvent.postponement(raw, grab));
        NameGrabbingPatterns.put(
                CommitteeAmendmentPattern,
                (raw, grab) -> new CommitteeAmendmentFiledBillEvent(raw, grab));

    }

    @Override
    public BillEventData parse(BillEvent billEvent) {
        String rawContents = billEvent.getRawContents();

        for(Map.Entry<Pattern, BiFunction<String,String, BillEventData>> parserEntry : NameGrabbingPatterns.entrySet()){
            Matcher matcher = parserEntry.getKey().matcher(rawContents);
            if( matcher.matches() ){
                String grabbed = matcher.group(1);
                return parserEntry.getValue().apply(rawContents, grabbed);
            }

        }

        return new UnclassifiedEventData(rawContents);
    }
}
