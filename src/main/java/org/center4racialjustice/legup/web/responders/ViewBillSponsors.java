package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillHistory;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.LegislatorBillAction;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ViewBillSponsors implements Responder {

    private final BillPersistence billPersistence;

    public ViewBillSponsors(ConnectionPool connectionPool) {
        this.billPersistence = new BillPersistence(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        long billId = submission.getLongRequestParameter("bill_id");

        BillHistory billHistory = billPersistence.loadBillHistory(billId);

        Bill bill = billHistory.getBill();

        List<Legislator> houseSponsors = billHistory.getSponsors(Chamber.House);
        List<Legislator> senateSponsors = billHistory.getSponsors(Chamber.Senate);

        List<Legislator> houseChiefSponsors = billHistory.getChiefSponsors(Chamber.House);
        List<Legislator> senateChiefSponsors = billHistory.getChiefSponsors(Chamber.Senate);

        HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(),
                submission.getLoggedInUser(), navLinks(billId, bill.getSession()));

        if( houseChiefSponsors.size() > 0 ){
            response.putVelocityData("chief_house_sponsor", houseChiefSponsors.get(0));
        }
        if( senateChiefSponsors.size() > 0 ){
            response.putVelocityData("chief_senate_sponsor", senateChiefSponsors.get(0));
        }

        response.putVelocityData("house_sponsors", houseSponsors);
        response.putVelocityData("senate_sponsors", senateSponsors);
        response.putVelocityData("bill", bill);

        return response;
    }

    private List<NavLink> navLinks(long billId, long sessionNumber){
        return Arrays.asList(
                new NavLink("Bills Index", "/legup/view_bills?session_number=" + sessionNumber),
                new NavLink("View Votes", "/legup/view_bill_votes?bill_id=" + billId)
        );
    }

}
