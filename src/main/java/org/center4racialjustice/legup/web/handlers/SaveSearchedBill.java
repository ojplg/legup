package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.BillSaveResults;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SaveSearchedBill implements Handler {

    private final ConnectionPool connectionPool;

    public SaveSearchedBill(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        String oneTimeKey = request.getParameter("oneTimeKey");

        BillSearchResults billSearchResults = (BillSearchResults) legupSession
                .getObject(LegupSession.BillSearchResultsKey, oneTimeKey);

        BillPersistence billPersistence = new BillPersistence(connectionPool);
        BillSaveResults billSaveResults = billPersistence.saveParsedData(billSearchResults);

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("bill", billSaveResults.getBill());
        velocityContext.put("sponsorSaveResults", billSaveResults.getSponsorSaveResults());
        velocityContext.put("billSaveResults", billSaveResults);
        return velocityContext;
    }
}
