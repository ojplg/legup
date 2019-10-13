package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillHistory;
import org.center4racialjustice.legup.domain.BillSaveResults;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.illinois.BillSearcherParser;
import org.center4racialjustice.legup.illinois.LegislationType;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.service.BillStatusComputer;
import org.center4racialjustice.legup.web.ContinueLegupResponse;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.SecuredResponder;

import java.util.Collections;

public class HardReloadBill implements SecuredResponder {

    private static final Logger log = LogManager.getLogger(HardReloadBill.class);

    private final BillPersistence billPersistence;
    private final ConnectionPool connectionPool;
    private final NameParser nameParser;

    public HardReloadBill(ConnectionPool connectionPool, NameParser nameParser) {
        this.connectionPool  = connectionPool;
        this.billPersistence = new BillPersistence(connectionPool);
        this.nameParser = nameParser;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        long billId = submission.getLongRequestParameter("bill_id");

        log.info("Beginning hard reload of bill " + billId);
        Bill bill = billPersistence.loadBill(billId);

        log.info("Deleting history for " + bill);
        billPersistence.deleteHistory(bill);

        BillSearchResults billSearchResults = doSearch(bill.getLegislationType(), bill.getNumber());
        BillHistory billHistory = billPersistence.loadBillHistory(bill.getBillIdentity());

        BillStatusComputer billStatusComputer = new BillStatusComputer(billSearchResults, billHistory);

        BillSaveResults billSaveResults = billPersistence.saveParsedData(billStatusComputer, true);

        log.info("saved " + billSaveResults);

        return new ContinueLegupResponse(ViewBillHistory.class,
                Collections.singletonMap("bill_id",  submission.getParameter("bill_id")));
    }

    private BillSearchResults doSearch(LegislationType legislationType, Long number){
        BillSearcherParser billSearcherParser = new BillSearcherParser(connectionPool, nameParser);
        BillSearchResults billSearchResults = billSearcherParser.doFullSearch(legislationType, number);
        return billSearchResults;
    }


    @Override
    public boolean permitted(LegupSubmission legupSubmission) {
        return legupSubmission.isSuperUserRequest();
    }
}
