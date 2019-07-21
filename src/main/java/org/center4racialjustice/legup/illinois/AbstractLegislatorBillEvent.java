package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.Name;

import java.util.regex.Matcher;

public abstract class AbstractLegislatorBillEvent extends AbstractBillEvent {

    private final String rawLegislatorName;
    private final Name parsedLegislatorName;
    private final BillActionType billActionType;

    public AbstractLegislatorBillEvent(BillEvent underlyingEvent,
                                       String rawLegislatorName,
                                       Name parsedLegislatorName,
                                       BillActionType billActionType) {
        super(underlyingEvent);
        this.rawLegislatorName = rawLegislatorName;
        this.parsedLegislatorName = parsedLegislatorName;
        this.billActionType = billActionType;
    }

    @Override
    public String getRawLegislatorName() {
        return rawLegislatorName;
    }

    @Override
    public Name getParsedLegislatorName() { return parsedLegislatorName; }

    @Override
    public BillActionType getBillActionType() {
        return billActionType;
    }

    @Override
    public String toString(){
        return "AbstractLegislatorBillEvent extends + " + super.toString()
                + ", rawLegislatorName=" + rawLegislatorName
                + ", parsedLegislatorName=" + parsedLegislatorName
                + ", legislatorMemberId=" + getLegislatorMemberID()
                + ", billActionType=" + billActionType;
    }

    @Override
    public String getLegislatorMemberID(){
        Matcher matcher = MemberHtmlParser.MemberIdExtractionPattern.matcher(getLink());
        if( matcher.matches() ){
            return matcher.group(1);
        }
        return null;
    }

}
