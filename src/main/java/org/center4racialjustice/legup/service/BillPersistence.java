package org.center4racialjustice.legup.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillActionLoadDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
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

import java.util.Collections;
import java.util.List;

public class BillPersistence {

    private final Logger log = LogManager.getLogger(BillPersistence.class);

    private final ConnectionPool connectionPool;

    public BillPersistence(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public List<BillActionLoad> checkForPriorLoads(BillSearchResults billSearchResults){
        try (ConnectionWrapper connection=connectionPool.getWrappedConnection()) {
            Bill parsedBill = billSearchResults.getBill();
            log.info("Checking for prior loads for " + parsedBill.getChamber() + "." + parsedBill.getNumber() + " session " + parsedBill.getSession());
            BillDao billDao = new BillDao(connection);
            Bill dbBill = billDao.readBySessionChamberAndNumber(parsedBill);
            if (dbBill == null) {
                return Collections.emptyList();
            } else {
                BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
                return billActionLoadDao.readByBill(dbBill);
            }
        }
    }

    public BillSaveResults saveParsedData(BillSearchResults billSearchResults) {
        BillSaveResults results = connectionPool.runAndCommit(connection -> {
            BillDao billDao = new BillDao(connection);
            BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);

            Bill parsedBill = billSearchResults.getBill();
            billDao.insert(parsedBill);

            BillActionLoad billActionLoad = BillActionLoad.create(parsedBill, billSearchResults.getUrl(), billSearchResults.getChecksum());
            billActionLoadDao.insert(billActionLoad);

            SponsorSaveResults sponsorsSaved = saveSponsors(connection, billActionLoad, billSearchResults);
            log.info("Saved sponsors: " + sponsorsSaved);

            BillActionLoad houseLoad = billSearchResults.createHouseBillActionLoad(parsedBill);
            billActionLoadDao.insert(houseLoad);
            int houseVotesSaved = saveVotes(connection, houseLoad, billSearchResults.getHouseVotes());
            log.info("Saved " + houseVotesSaved + " house votes");

            BillActionLoad senateLoad = billSearchResults.createSenateBillActionLoad(parsedBill);
            billActionLoadDao.insert(senateLoad);
            int senateVotesSaved = saveVotes(connection, senateLoad, billSearchResults.getSenateVotes());
            log.info("Saved " + senateVotesSaved + " senate votes");

            BillActionLoads billActionLoads = new BillActionLoads(billActionLoad, houseLoad, senateLoad);
            return new BillSaveResults(parsedBill, houseVotesSaved, senateVotesSaved, sponsorsSaved, billActionLoads);
        });
        return results;
    }

    public LookupTable<Legislator, String, String> generateBillActionSummary(long billId){
        try (ConnectionWrapper connection=connectionPool.getWrappedConnection()) {

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
        }
    }

    private int saveVotes(ConnectionWrapper connection, BillActionLoad billActionLoad, List<CollatedVote> votes) {
        BillActionDao billActionDao = new BillActionDao(connection);

        int savedCount = 0;
        for (CollatedVote collatedVote : votes) {
            Vote vote = collatedVote.asVote(billActionLoad);
            BillAction billAction = BillAction.fromVote(vote);
            billActionDao.insert(billAction);
            savedCount++;
        }
        return savedCount;
    }

    private SponsorSaveResults saveSponsors(ConnectionWrapper connection, BillActionLoad billActionLoad, BillSearchResults billSearchResults) {
        BillActionDao billActionDao = new BillActionDao(connection);

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
