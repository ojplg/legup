package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventCommitteeData;
import org.center4racialjustice.legup.domain.BillEventInterpreter;
import org.center4racialjustice.legup.domain.BillEventLegislatorData;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.domain.RawBillEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillEventParser implements BillEventInterpreter {

    public static final Pattern CommitteeIdExtractionPattern = Pattern.compile(".*committeeID=(\\d+).*");

    private static final Pattern FiledWithClerkPattern =
            Pattern.compile("Filed with (?:the Clerk|Secretary) by (?:Sen|Rep). (.*)");

    private static final Pattern AddedChiefSponsorPattern =
            Pattern.compile("Added (?:as )?Chief Co-Sponsor (?:Sen|Rep). (.*)");

    private static final Pattern AddedSponsorPattern =
            Pattern.compile("Added (?:as )?Co-Sponsor (?:Sen|Rep). (.*)");

    private static final Pattern RemovedSponsorPattern =
            Pattern.compile("Removed (?:as )?Co-Sponsor (?:Sen|Rep). (.*)");

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

    private static final List<String> CommitteeVotePrefixes = Arrays.asList(
            "Do Pass as Amended / Short Debate",
            "Recommends Do Pass",
            "Reported Back To",
            "Do Pass / Short Debate",
            "Do Pass / Short Debate|Do Pass as Amended",
            "House Floor Amendment No. \\d+ Recommends Be Adopted",
            "Senate Floor Amendment No. \\d+ Recommends Be Adopted",
            "Do Pass"
    );

    private static final Pattern CommitteeVotePattern =
            Pattern.compile("(?:" +  String.join("|", CommitteeVotePrefixes)  +  ")? ([\\w\\s-/\\&]+); \\d\\d\\d-\\d\\d\\d-\\d\\d\\d$");

    private static final Pattern VotePattern =
            Pattern.compile(".*\\d\\d\\d-\\d\\d\\d-\\d\\d\\d$");

    private final Map<Pattern, BiFunction<RawBillEvent, String, BillEvent>> nameGrabbingEventBuilders = new HashMap<>();
    private final Map<Pattern, Function<RawBillEvent, BillEvent>> noGrabEventBuilders = new HashMap<>();

    private final NameParser nameParser;

    public BillEventParser() {
        this(new NameParser());
    }

    public BillEventParser(NameParser nameParser) {
        this.nameParser = nameParser;

        noGrabEventBuilders.put(
                VotePattern,
                this::forVoteEvent);

        nameGrabbingEventBuilders.put(FiledWithClerkPattern,
                (rawEvent, rawName) -> forLegislatorBillEvent(rawEvent, rawName, BillActionType.INTRODUCE));
        nameGrabbingEventBuilders.put(AddedChiefSponsorPattern,
                (rawEvent, rawName) -> forLegislatorBillEvent(rawEvent, rawName, BillActionType.CHIEF_SPONSOR));
        nameGrabbingEventBuilders.put(ChiefSenateSponsorPattern,
                (rawEvent, rawName) -> forLegislatorBillEvent(rawEvent, rawName, BillActionType.CHIEF_SPONSOR));
        nameGrabbingEventBuilders.put(AddedAlternateChiefCoSponsorPattern,
                (rawEvent, rawName) -> forLegislatorBillEvent(rawEvent, rawName, BillActionType.CHIEF_SPONSOR));

        nameGrabbingEventBuilders.put(AddedSponsorPattern,
                (rawEvent, rawName) -> forLegislatorBillEvent(rawEvent, rawName, BillActionType.SPONSOR));
        nameGrabbingEventBuilders.put(AddedAlternateCoSponsorPattern,
                (rawEvent, rawName) -> forLegislatorBillEvent(rawEvent, rawName, BillActionType.SPONSOR));

        nameGrabbingEventBuilders.put(RemovedSponsorPattern,
                (rawEvent, rawName) -> forLegislatorBillEvent(rawEvent, rawName, BillActionType.REMOVE_SPONSOR));

        nameGrabbingEventBuilders.put(CommitteeAmendmentPattern,
                (rawEvent, rawName) -> forLegislatorBillEvent(rawEvent, rawName, BillActionType.COMMITTEE_AMENDMENT_FILED));

        nameGrabbingEventBuilders.put(CommitteeReferralPattern,
                (rawEvent, rawName) -> forCommitteeBillEvent(rawEvent, rawName, BillActionType.COMMITTEE_REFERRAL));
        nameGrabbingEventBuilders.put(CommitteeAssignmentPattern,
                (rawEvent, rawName) -> forCommitteeBillEvent(rawEvent, rawName, BillActionType.COMMITTEE_ASSIGNMENT));
        nameGrabbingEventBuilders.put(CommitteePostponementPattern,
                (rawEvent, rawName) -> forCommitteeBillEvent(rawEvent, rawName, BillActionType.COMMITTEE_POSTPONEMENT));
        nameGrabbingEventBuilders.put(CommitteeVotePattern,
                (rawEvent, rawName) -> forCommitteeBillEvent(rawEvent, rawName, BillActionType.VOTE));

    }

    public BillEvent parse(RawBillEvent rawBillEvent){
        String rawContents = rawBillEvent.getRawContents();

        for(Map.Entry<Pattern, BiFunction<RawBillEvent, String, BillEvent>> parserEntry : nameGrabbingEventBuilders.entrySet()){
            Matcher matcher = parserEntry.getKey().matcher(rawContents);
            if( matcher.matches() ){
                String grabbed = matcher.group(1);
                return parserEntry.getValue().apply(rawBillEvent, grabbed);
            }
        }

        for(Map.Entry<Pattern, Function<RawBillEvent, BillEvent>> parserEntry : noGrabEventBuilders.entrySet()){
            Matcher matcher = parserEntry.getKey().matcher(rawContents);
            if( matcher.matches() ){
                return parserEntry.getValue().apply(rawBillEvent);
            }
        }

        return new BillEvent(rawBillEvent, BillActionType.UNCLASSIFIED, BillEventLegislatorData.EMPTY, BillEventCommitteeData.EMPTY);
    }

    private BillEvent forCommitteeBillEvent(RawBillEvent rawBillEvent, String rawCommitteeName, BillActionType billActionType){
        String committeeId = null;

        Matcher commiteeIdMatcher = CommitteeIdExtractionPattern.matcher(rawBillEvent.getLink());
        if( commiteeIdMatcher.matches() ){
            committeeId = commiteeIdMatcher.group(1);
        }
        BillEventCommitteeData billEventCommitteeData = BillEventCommitteeData.builder()
                .rawCommitteeName(rawCommitteeName)
                .committeeId(committeeId)
                .build();
        return new BillEvent(rawBillEvent, billActionType, BillEventLegislatorData.EMPTY, billEventCommitteeData);
    }

    private BillEvent forLegislatorBillEvent(RawBillEvent rawBillEvent, String rawLegislatorName, BillActionType billActionType){
        Name parsedLegislatorName = nameParser.fromRegularOrderString(rawLegislatorName);
        String memberId = null;

        Matcher memberIdMatcher = MemberHtmlParser.MemberIdExtractionPattern.matcher(rawBillEvent.getLink());
        if( memberIdMatcher.matches() ){
            memberId =  memberIdMatcher.group(1);
        }
        BillEventLegislatorData billEventLegislatorData = BillEventLegislatorData.builder()
                .rawLegislatorName(rawLegislatorName)
                .parsedLegislatorName(parsedLegislatorName)
                .legislatorMemberId(memberId)
                .build();
        return new BillEvent(rawBillEvent, billActionType, billEventLegislatorData, BillEventCommitteeData.EMPTY);
    }

    private BillEvent forVoteEvent(RawBillEvent rawBillEvent){
        return new BillEvent(rawBillEvent, BillActionType.VOTE, BillEventLegislatorData.EMPTY, BillEventCommitteeData.EMPTY);
    }
}
