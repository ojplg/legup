package org.center4racialjustice.legup.web.handlers;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.BillSaveResults;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

public class SaveSearchedBill implements Responder {

    private final ConnectionPool connectionPool;

    public SaveSearchedBill(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        BillSearchResults billSearchResults = (BillSearchResults) submission
                .getObject(LegupSession.BillSearchResultsKey);

        BillPersistence billPersistence = new BillPersistence(connectionPool);
        BillSaveResults billSaveResults = billPersistence.saveParsedData(billSearchResults);

        LegupResponse response = new LegupResponse(this.getClass());
        response.putVelocityData("bill", billSaveResults.getBill());
        response.putVelocityData("sponsorSaveResults", billSaveResults.getSponsorSaveResults());
        response.putVelocityData("billSaveResults", billSaveResults);
        return response;
    }
}
