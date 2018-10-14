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
import org.center4racialjustice.legup.domain.BillSaveResults;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.SponsorSaveResults;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.illinois.BillActionLoads;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.illinois.CollatedVote;
import org.center4racialjustice.legup.illinois.SponsorName;
import org.center4racialjustice.legup.illinois.SponsorNames;
import org.center4racialjustice.legup.util.LookupTable;
import org.center4racialjustice.legup.util.Tuple;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class BillPersistence {

    private final Logger log = LogManager.getLogger(BillPersistence.class);

    private final ConnectionPool connectionPool;

    public BillPersistence(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Tuple<Bill,List<BillActionLoad>> checkForPriorLoads(Bill parsedBill){
        return connectionPool.useConnection( connection -> {
            log.info("Checking for prior loads for " + parsedBill.getChamber() + "." + parsedBill.getNumber() + " session " + parsedBill.getSession());
            BillDao billDao = new BillDao(connection);
            List<BillActionLoad> loads;
            Bill dbBill = billDao.readBySessionChamberAndNumber(parsedBill);
            if (dbBill == null) {
                loads = Collections.emptyList();
            } else {
                BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
                loads = billActionLoadDao.readByBill(dbBill);
            }
            return new Tuple<>(dbBill, loads);
        });
    }

    public BillSaveResults saveParsedData(BillSearchResults billSearchResults) {
        BillSaveResults results = connectionPool.runAndCommit(connection -> {
            BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);

            Bill bill;
            BillActionLoad billActionLoad;

            if( billSearchResults.getBillHtmlLoadStatus() == BillSearchResults.MatchStatus.NoPriorValues ){
                Tuple<Bill, BillActionLoad> billSaveTuple = insertNewBill( connection,
                    billSearchResults.getParsedBill(), billSearchResults.getUrl(), billSearchResults.getChecksum() );
                bill = billSaveTuple.getFirst();
                billActionLoad = billSaveTuple.getSecond();
            } else if ( billSearchResults.getBillHtmlLoadStatus() == BillSearchResults.MatchStatus.MatchedValues ){
                bill = billSearchResults.getSavedBill();
                billActionLoad = billSearchResults.getBillHtmlLoad();
            } else if ( billSearchResults.getBillHtmlLoadStatus() == BillSearchResults.MatchStatus.UnmatchedValues ){
                Tuple<Bill, BillActionLoad> billSaveTuple = updateBill( connection,
                        billSearchResults.getUpdatedBill(),  billSearchResults.getBillHtmlLoad(),
                        billSearchResults.getUrl(), billSearchResults.getChecksum() );
                bill = billSaveTuple.getFirst();
                billActionLoad = billSaveTuple.getSecond();
            } else {
                throw new RuntimeException("Cannot persist match status " + billSearchResults.getBillHtmlLoadStatus());
            }

            SponsorSaveResults sponsorsSaved = saveSponsors(connection, billActionLoad, billSearchResults);
            log.info("Saved sponsors: " + sponsorsSaved);

            int houseVotesSaved = 0;
            BillActionLoad houseLoad = billSearchResults.getHouseVotesLoad();
            if( billSearchResults.getHouseVotesLoadStatus() == BillSearchResults.MatchStatus.NoPriorValues ) {
                houseLoad = billSearchResults.createHouseBillActionLoad(bill);
                billActionLoadDao.insert(houseLoad);
                houseVotesSaved = saveVotes(connection, houseLoad, billSearchResults.getHouseVotes());
            } else if ( billSearchResults.getHouseVotesLoadStatus() == BillSearchResults.MatchStatus.UnmatchedValues ){
                houseLoad = billSearchResults.updateHouseBillActionLoad();
                billActionLoadDao.update(houseLoad);
                houseVotesSaved = saveVotes(connection, houseLoad, billSearchResults.getHouseVotes());
            }
            log.info("Saved " + houseVotesSaved + " house votes");

            int senateVotesSaved = 0;
            BillActionLoad senateLoad = billSearchResults.getSenateVotesLoad();

            if ( billSearchResults.getSenateVotesLoadStatus() == BillSearchResults.MatchStatus.NoPriorValues ) {
                senateLoad = billSearchResults.createSenateBillActionLoad(bill);
                billActionLoadDao.insert(senateLoad);
                senateVotesSaved = saveVotes(connection, senateLoad, billSearchResults.getSenateVotes());
            } else if ( billSearchResults.getSenateVotesLoadStatus() == BillSearchResults.MatchStatus.UnmatchedValues){
               senateLoad = billSearchResults.updateSenateBillActionLoad();
               billActionLoadDao.update(senateLoad);
               senateVotesSaved = saveVotes(connection, senateLoad, billSearchResults.getSenateVotes());
            }
            log.info("Saved " + senateVotesSaved + " senate votes");

            BillActionLoads billActionLoads = new BillActionLoads(billActionLoad, houseLoad, senateLoad);
            return new BillSaveResults(bill, houseVotesSaved, senateVotesSaved, sponsorsSaved, billActionLoads);
        });
        return results;
    }

    private Tuple<Bill, BillActionLoad> updateBill(Connection connection, Bill bill, BillActionLoad oldLoad, String url, long checkSum) {
        BillDao billDao = new BillDao(connection);
        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);

        billDao.update(bill);
        oldLoad.setCheckSum(checkSum);
        oldLoad.setLoadTime(LocalDateTime.now());
        oldLoad.setUrl(url);

        billActionLoadDao.update(oldLoad);

        return new Tuple<>(bill, oldLoad);
    }

    private Tuple<Bill, BillActionLoad> insertNewBill(Connection connection, Bill parsedBill, String url, long checkSum){
        BillDao billDao = new BillDao(connection);
        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);

        billDao.insert(parsedBill);

        BillActionLoad billActionLoad = BillActionLoad.create(parsedBill, url, checkSum);
        billActionLoadDao.insert(billActionLoad);

        return new Tuple<>(parsedBill, billActionLoad);
    }

    public LookupTable<Legislator, String, String> generateBillActionSummary(long billId){
        return connectionPool.useConnection(connection ->  {

            BillDao billDao = new BillDao(connection);
            BillActionDao billActionDao = new BillActionDao(connection);

            Bill bill = billDao.read(billId);

            List<BillAction> billActions = billActionDao.readByBill(bill);

            LookupTable<Legislator, String, String> billActionTable = new LookupTable<>();

            for (BillAction billAction : billActions) {
                Legislator leg = billAction.getLegislator();
                if (billAction.isVote()) {
                    Vote vote = billAction.asVote();
                    billActionTable.put(leg, "Vote", vote.getVoteSide().getDisplayString());
                } else {
                    billActionTable.put(leg, billAction.getBillActionType().getCode(), "Check");
                }
            }
            return billActionTable;
        });
    }

    private int saveVotes(Connection connection, BillActionLoad billActionLoad, List<CollatedVote> votes) {
        BillActionDao billActionDao = new BillActionDao(connection);

        deleteOldActionRecords(billActionDao, billActionLoad);

        int savedCount = 0;
        for (CollatedVote collatedVote : votes) {
            Vote vote = collatedVote.asVote(billActionLoad);
            BillAction billAction = BillAction.fromVote(vote);
            billActionDao.insert(billAction);
            savedCount++;
        }
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
        billAction.setLegislator(legislator);
        billAction.setBillActionLoad(billActionLoad);
        billAction.setBillActionType(billActionType);

        billActionDao.insert(billAction);
    }
}
