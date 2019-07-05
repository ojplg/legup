package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventInterpreter;

public class BillEventParser implements BillEventInterpreter {

    @Override
    public BillActionType readActionType(BillEvent billEvent) {
        String contents = billEvent.getRawContents();

        if( contents.startsWith("Filed with the Clerk")){
            return BillActionType.CHIEF_SPONSOR;
        }

        if ( contents.startsWith("First Reading") ){
            return BillActionType.FIRST_READING;
        }

        if ( contents.startsWith("Referred to ") ){
            return BillActionType.COMMITTEE_REFERRAL;
        }

        if ( contents.startsWith("Assigned to ") ){
            return BillActionType.COMMITTEE_ASSIGNMENT;
        }

        if( contents.startsWith("Added Chief Co-Sponsor ")
            || contents.startsWith("Added as Alternate Chief Co-Sponsor ")
            || contents.startsWith("Chief Senate Sponsor ")
            || contents.startsWith("Chief House Sponsor ")){
            return BillActionType.CHIEF_SPONSOR;
        }

        if ( contents.startsWith("Placed on Calendar ") ){
            return BillActionType.CALENDAR_SCHEDULING;
        } 

        if( contents.startsWith("Added Co-Sponsor ")
            || contents.startsWith("Added as Alternate Co-Sponsor ")){
            return BillActionType.SPONSOR;
        }

        if( contents.contains("Amendment") ){
            return BillActionType.AMENDMENT;
        }

        return null;
    }
}
