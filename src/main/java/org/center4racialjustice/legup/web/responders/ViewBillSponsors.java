package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
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

    private final ConnectionPool connectionPool;

    public ViewBillSponsors(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        long billId = submission.getLongRequestParameter("bill_id");

        return connectionPool.useConnection(connection -> {
            BillDao billDao = new BillDao(connection);

            Bill bill = billDao.read(billId);

            BillActionDao billActionDao = new BillActionDao(connection);

            List<BillAction> billActions =  billActionDao.readByBill(bill);
            List<Legislator> sponsors = billActions.stream()
                    .filter(act -> act.getBillActionType().equals(BillActionType.SPONSOR))
                    .map(BillAction::getLegislator)
                    .collect(Collectors.toList());

            Tuple<List<Legislator>, List<Legislator>> sponsorsTuple =
                    Lists.divide(sponsors, leg -> leg.getChamber().equals(Chamber.House));
            List<Legislator> houseSponsors = sponsorsTuple.getFirst();
            List<Legislator> senateSponsors = sponsorsTuple.getSecond();
            Collections.sort(houseSponsors);
            Collections.sort(senateSponsors);

            List<Legislator> chiefSponsors = billActions.stream()
                    .filter(act -> act.getBillActionType().equals(BillActionType.CHIEF_SPONSOR))
                    .map(BillAction::getLegislator)
                    .collect(Collectors.toList());
            Tuple<List<Legislator>, List<Legislator>> chiefSponsorsTuple =
                    Lists.divide(chiefSponsors, leg -> leg.getChamber().equals(Chamber.House));
            List<Legislator> chiefHouseSponsors = chiefSponsorsTuple.getFirst();
            List<Legislator> chiefSenateSponsors = chiefSponsorsTuple.getSecond();

            HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(),
                    submission.getLoggedInUser(), navLinks(billId, bill.getSession()));

            if( chiefHouseSponsors.size() > 0 ){
                response.putVelocityData("chief_house_sponsor", chiefHouseSponsors.get(0));
            }
            if( chiefSenateSponsors.size() > 0 ){
                response.putVelocityData("chief_senate_sponsor", chiefSenateSponsors.get(0));
            }

            response.putVelocityData("house_sponsors", houseSponsors);
            response.putVelocityData("senate_sponsors", senateSponsors);
            response.putVelocityData("bill", bill);

            return response;
        });
    }

    private List<NavLink> navLinks(long billId, long sessionNumber){
        return Arrays.asList(
                new NavLink("Bills Index", "/legup/view_bills?session_number=" + sessionNumber),
                new NavLink("View Votes", "/legup/view_bill_votes?bill_id=" + billId)
        );
    }

}
