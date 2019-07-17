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
            Pattern.compile("Filed with the Clerk by Rep. (.*)");

    private static final Pattern AddedChiefSponsorPattern =
            Pattern.compile("Added Chief Co-Sponsor Rep. (.*)");

    private static final Pattern AddedSponsorPattern =
            Pattern.compile("Added Co-Sponsor Rep. (.*)");

    private static final Pattern ChiefSenateSponsorPattern =
            Pattern.compile("Chief Senate Sponsor Sen. (.*)");

    private static final Pattern AddedAlternateCoSponsorPattern =
            Pattern.compile("Added as Alternate Co-Sponsor Sen. (.*)");

    private static final Pattern AddedAlternateChiefCoSponsorPattern =
            Pattern.compile("Added as Alternate Chief Co-Sponsor Sen. (.*)");


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

//        if( contents.startsWith("Filed with the Clerk")
//                || contents.startsWith("Filed with Secretary ")){
//            return BillActionType.CHIEF_SPONSOR;
//        }
//
//        if ( contents.startsWith("First Reading") ){
//            return BillActionType.FIRST_READING;
//        }
//
//        if ( contents.startsWith("Referred to ") ){
//            return BillActionType.COMMITTEE_REFERRAL;
//        }
//
//        if ( contents.startsWith("Assigned to ") ){
//            return BillActionType.COMMITTEE_ASSIGNMENT;
//        }
//
//        if ( contents.startsWith("Postponed ") ){
//            return BillActionType.POSTPONED;
//        }
//
//        if( contents.startsWith("Added Chief Co-Sponsor ")
//                || contents.startsWith("Added as Chief Co-Sponsor ")
//                || contents.startsWith("Added as Alternate Chief Co-Sponsor ")
//                || contents.startsWith("Added Alternate Chief Co-Sponsor ")
//                || contents.startsWith("Chief Senate Sponsor ")
//                || contents.startsWith("Chief House Sponsor ")){
//            return BillActionType.CHIEF_SPONSOR;
//        }
//
//        if ( contents.startsWith("Placed on Calendar ") ){
//            return BillActionType.CALENDAR_SCHEDULING;
//        }
//
//        if( contents.startsWith("Added Co-Sponsor ")
//                || contents.startsWith("Added as Co-Sponsor ")
//                || contents.startsWith("Added Alternate Co-Sponsor ")
//                || contents.startsWith("Added as Alternate Co-Sponsor ")){
//            return BillActionType.SPONSOR;
//        }
//
//        if( contents.contains("Amendment") ){
//            return BillActionType.AMENDMENT;
//        }
//
//        if( contents.contains("Deadline Established") ){
//            return BillActionType.DEADLINE_ESTABLISHED;
//        }


        return null;
    }
}
