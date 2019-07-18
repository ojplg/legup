package org.center4racialjustice.legup.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillActionLoadDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillHistory;
import org.center4racialjustice.legup.domain.BillSaveResults;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.LegislatorBillAction;
import org.center4racialjustice.legup.domain.LegislatorBillActionType;
import org.center4racialjustice.legup.domain.SponsorSaveResults;
import org.center4racialjustice.legup.illinois.BillActionLoads;
import org.center4racialjustice.legup.illinois.BillIdentity;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.illinois.BillVotesResults;
import org.center4racialjustice.legup.illinois.CollatedVote;
import org.center4racialjustice.legup.illinois.SponsorName;
import org.center4racialjustice.legup.illinois.SponsorNames;
import org.center4racialjustice.legup.util.Dates;
import org.center4racialjustice.legup.util.LookupTable;
import org.center4racialjustice.legup.util.Tuple;

import java.sql.Connection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BillPersistence {

    private final Logger log = LogManager.getLogger(BillPersistence.class);

    private final ConnectionPool connectionPool;

    public BillPersistence(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public BillHistory loadBillHistory(long billId){
        return connectionPool.useConnection( connection -> {
            log.info("Loading bill history for " + billId);

            BillDao billDao = new BillDao(connection);
            Bill bill = billDao.read(billId);

            if ( bill == null ){
                return BillHistory.EMPTY;
            }

            BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
            List<BillActionLoad> loads = billActionLoadDao.readByBillId(billId);

            BillActionDao billActionDao = new BillActionDao(connection);
            List<BillAction> actions = billActionDao.readByBill(bill);

            return new BillHistory(bill, loads, actions);
        });
    }

    public BillHistory loadBillHistory(BillIdentity billIdentity) {
        return connectionPool.useConnection(connection -> {
            log.info("Loading bill history for " + billIdentity);

            BillDao billDao = new BillDao(connection);
            Bill bill = billDao.read(billIdentity);

            if ( bill == null ){
                return BillHistory.EMPTY;
            }

            BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
            List<BillActionLoad> loads = billActionLoadDao.readByBillId(bill.getId());

            BillActionDao billActionDao = new BillActionDao(connection);
            List<BillAction> actions = billActionDao.readByBill(bill);

            return new BillHistory(bill, loads, actions);

        });
    }

    public BillSaveResults saveParsedData(BillSearchResults billSearchResults, boolean forceSave) {

        log.info("Doing save of " + billSearchResults.getBillIdentity() + " with force flag " + forceSave);

        BillHistory billHistory = loadBillHistory(billSearchResults.getBillIdentity());
        BillStatusComputer billStatusComputer = new BillStatusComputer(billSearchResults, billHistory);

        return connectionPool.runAndCommit(connection -> {
            BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);

            Bill bill;
            BillActionLoad billActionLoad;

            SponsorSaveResults sponsorsSaved;

            if( ! billStatusComputer.hasHistory() ){
                bill = insertNewBill( connection, billSearchResults.getParsedBill());
                billActionLoad = insertNewBillLoadAction(connection, bill, billSearchResults.getUrl(), billSearchResults.getChecksum() );
            } else {
                bill = billHistory.getBill();
                if( billStatusComputer.hasUnpersistedEvents() ){
                    billActionLoad = insertNewBillLoadAction(connection, bill, billSearchResults.getUrl(), billSearchResults.getChecksum());
                }
            }

            for( BillEvent billEvent : billStatusComputer.unpersistedEvents() ){

            }

//            if( billSearchResults.getBillHtmlLoadStatus() == BillSearchResults.MatchStatus.NoPriorValues ){
//                sponsorsSaved = saveSponsors(connection, billActionLoad, billSearchResults);
//                log.info("Saved sponsors: " + sponsorsSaved);
//            } else if ( billSearchResults.getBillHtmlLoadStatus() == BillSearchResults.MatchStatus.MatchedValues ){
//                bill = billSearchResults.getSavedBill();
//                billActionLoad = billSearchResults.getBillHtmlLoad();
//                sponsorsSaved = SponsorSaveResults.EMPTY;
//            } else if ( billSearchResults.getBillHtmlLoadStatus() == BillSearchResults.MatchStatus.UnmatchedValues
//                        || forceSave ){
//                Tuple<Bill, BillActionLoad> billSaveTuple = updateBill( connection,
//                        billSearchResults.getUpdatedBill(),  billSearchResults.getBillHtmlLoad(),
//                        billSearchResults.getUrl(), billSearchResults.getChecksum() );
//                bill = billSaveTuple.getFirst();
//                billActionLoad = billSaveTuple.getSecond();
//                sponsorsSaved = saveSponsors(connection, billActionLoad, billSearchResults);
//                log.info("Saved sponsors: " + sponsorsSaved);
//            } else {
//                throw new RuntimeException("Cannot persist match status " + billSearchResults.getBillHtmlLoadStatus());
//            }
//
//            int houseVotesSaved = 0;
//
//            int senateVotesSaved = 0;
//
//            List<BillActionLoad> loads = new ArrayList<>();
//            loads.add(billActionLoad);
//            for(String key : billSearchResults.generateSearchedVoteLoadKeys()){
//                BillSearchResults.MatchStatus matchStatus = billSearchResults.getVoteMatchStatus(key);
//                if( matchStatus == BillSearchResults.MatchStatus.NoPriorValues){
//                    BillActionLoad load = billSearchResults.createVoteBillActionLoad(bill, key);
//                    BillVotesResults billVotesResults = billSearchResults.getBillVotesResults(key);
//                    billActionLoadDao.insert(load);
//                    saveVotes(connection, load, billVotesResults);
//                } else if ( matchStatus == BillSearchResults.MatchStatus.UnmatchedValues || forceSave ){
//                    log.warn("Need to resave: " + key);
//                }
//            }
//
//            BillActionLoads billActionLoads = new BillActionLoads(loads);
            // TODO: counts are all goofed up
            return new BillSaveResults(bill, 0,0, null, null);
        });
    }

    private Tuple<Bill, BillActionLoad> updateBill(Connection connection, Bill bill, BillActionLoad oldLoad, String url, long checkSum) {
        BillDao billDao = new BillDao(connection);
        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);

        billDao.update(bill);
        oldLoad.setCheckSum(checkSum);
        oldLoad.setLoadInstant(Instant.now());
        oldLoad.setUrl(url);

        billActionLoadDao.update(oldLoad);

        return new Tuple<>(bill, oldLoad);
    }

    private Bill insertNewBill(Connection connection, Bill bill) {
        BillDao billDao = new BillDao(connection);
        billDao.insert(bill);
        return bill;
    }

    private BillActionLoad insertNewBillLoadAction(Connection connection, Bill bill, String url, long checkSum){
        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
        BillActionLoad billActionLoad = BillActionLoad.create(bill, url, checkSum);
        billActionLoadDao.insert(billActionLoad);
        return  billActionLoad;
    }

    public LookupTable<Legislator, String, String> generateBillActionSummary(long billId){
        return connectionPool.useConnection(connection ->  {

            BillDao billDao = new BillDao(connection);
            BillActionDao billActionDao = new BillActionDao(connection);

            Bill bill = billDao.read(billId);

            List<BillAction> billActions = billActionDao.readByBill(bill);

            LookupTable<Legislator, String, String> billActionTable = new LookupTable<>();

            for (BillAction billAction : billActions) {
                for (LegislatorBillAction legislatorBillAction : billAction.getLegislatorBillActions()) {
                    Legislator leg = legislatorBillAction.getLegislator();
                    // FIXME: Need to do the right thing with votes here
                    if (billAction.isVote()) {
                        billActionTable.put(leg, "Vote",legislatorBillAction.getVoteSide().getDisplayString());
                    } else {
                        billActionTable.put(leg, billAction.getBillActionType().getCode(), "Check");
                    }
                }
            }
            return billActionTable;
        });
    }

    private int saveVotes(Connection connection, BillActionLoad billActionLoad, BillVotesResults billVotesResults) {
        BillActionDao billActionDao = new BillActionDao(connection);

        deleteOldActionRecords(billActionDao, billActionLoad);

        List<CollatedVote> votes = billVotesResults.getCollatedVotes();

        int savedCount = 0;

        List<LegislatorBillAction> legislatorBillActions = new ArrayList<>();
        for (CollatedVote collatedVote : votes) {
            LegislatorBillAction legislatorBillAction = new LegislatorBillAction();
            legislatorBillAction.setLegislator(collatedVote.getLegislator());
            legislatorBillAction.setVoteSide(collatedVote.getVoteSide());
            legislatorBillAction.setLegislatorBillActionType(LegislatorBillActionType.VOTE);
            legislatorBillActions.add(legislatorBillAction);
        }

        BillAction billAction = new BillAction();
        billAction.setBillActionType(BillActionType.VOTE);
        billAction.setBill(billActionLoad.getBill());
        billAction.setLegislatorBillActions(legislatorBillActions);
        billAction.setRawActionData(billVotesResults.getRawData());
        billAction.setActionDate(Dates.instantOf(billVotesResults.getActionDate()));

        billActionDao.insert(billAction);

        return savedCount;
    }

    private SponsorSaveResults saveSponsors(Connection connection, BillActionLoad billActionLoad, BillSearchResults billSearchResults) {
        BillActionDao billActionDao = new BillActionDao(connection);

        deleteOldActionRecords(billActionDao, billActionLoad);

        SponsorNames sponsorNames = billSearchResults.getSponsorNames();

        Legislator chiefHouseSponsor = null;
        Legislator chiefSenateSponsor = null;
        int houseSponsorCount = 0;
        int senateSponsorCount = 0;
        if (sponsorNames.getChiefHouseSponsor() != null && sponsorNames.getChiefHouseSponsor().isComplete()) {
            chiefHouseSponsor = sponsorNames.getChiefHouseSponsor().getLegislator();
            saveBillAction(billActionDao, chiefHouseSponsor, billActionLoad, BillActionType.CHIEF_SPONSOR);
        }
        if (sponsorNames.getChiefSenateSponsor() != null && sponsorNames.getChiefSenateSponsor().isComplete()) {
            chiefSenateSponsor = sponsorNames.getChiefSenateSponsor().getLegislator();
            saveBillAction(billActionDao, chiefSenateSponsor, billActionLoad, BillActionType.CHIEF_SPONSOR);
        }
        for (SponsorName sponsorName : sponsorNames.getHouseSponsors()) {
            if (sponsorName.isComplete()) {
                saveBillAction(billActionDao, sponsorName.getLegislator(), billActionLoad, BillActionType.SPONSOR);
                houseSponsorCount++;
            }
        }
        for (SponsorName sponsorName : sponsorNames.getSenateSponsors()) {
            if (sponsorName.isComplete()) {
                saveBillAction(billActionDao, sponsorName.getLegislator(), billActionLoad, BillActionType.SPONSOR);
                senateSponsorCount++;
            }
        }

        return new SponsorSaveResults(chiefHouseSponsor, chiefSenateSponsor, houseSponsorCount, senateSponsorCount);
    }

    private void deleteOldActionRecords(BillActionDao billActionDao, BillActionLoad billActionLoad){
        List<BillAction> oldActions = billActionDao.readByBillActionLoad(billActionLoad);
        for(BillAction billAction : oldActions){
            billActionDao.delete(billAction);
        }
    }

    private void saveBillAction(BillActionDao billActionDao, Legislator legislator,
                                BillActionLoad billActionLoad, BillActionType billActionType){
        BillAction billAction = new BillAction();
        billAction.setBill(billActionLoad.getBill());
        billAction.setBillActionLoad(billActionLoad);
        billAction.setBillActionType(billActionType);

        billActionDao.insert(billAction);
    }
}
