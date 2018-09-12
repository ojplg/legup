package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ViewBillSponsors implements Handler {

    private final ConnectionPool connectionPool;

    public ViewBillSponsors(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        String billIdParameter = request.getParameter("bill_id");
        long billId = Long.parseLong(billIdParameter);

        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()){
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

            VelocityContext velocityContext = new VelocityContext();

            if( chiefHouseSponsors.size() > 0 ){
                velocityContext.put("chief_house_sponsor", chiefHouseSponsors.get(0));
            }
            if( chiefSenateSponsors.size() > 0 ){
                velocityContext.put("chief_senate_sponsor", chiefSenateSponsors.get(0));
            }

            velocityContext.put("house_sponsors", houseSponsors);
            velocityContext.put("senate_sponsors", senateSponsors);
            velocityContext.put("bill", bill);

            return velocityContext;
        }

    }
}
