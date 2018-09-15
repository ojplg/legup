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
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.illinois.CollatedVote;
import org.center4racialjustice.legup.illinois.SponsorName;
import org.center4racialjustice.legup.illinois.SponsorNames;

import java.util.Collections;
import java.util.List;

public class BillPersistence {

    private final Logger log = LogManager.getLogger(BillPersistence.class);

    private final ConnectionPool connectionPool;

    public BillPersistence(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void checkPriorLoads(BillSearchResults billSearchResults){
        try (ConnectionWrapper connection=connectionPool.getWrappedConnection()) {
            Bill parsedBill = billSearchResults.getBill();
            BillDao billDao = new BillDao(connection);
            Bill dbBill = billDao.readBySessionChamberAndNumber(parsedBill.getSession(), parsedBill.getChamber(), parsedBill.getNumber());
            if (dbBill == null) {
                billSearchResults.setPriorLoads(Collections.emptyList());
            } else {
                BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
                List<BillActionLoad> priorLoads = billActionLoadDao.readByBill(dbBill);
                billSearchResults.setPriorLoads(priorLoads);
            }
        }
    }

    public Bill saveParsedData(BillSearchResults billSearchResults) {

        try (ConnectionWrapper connection=connectionPool.getWrappedConnection()){
            BillDao billDao = new BillDao(connection);
            BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);

            Bill parsedBill = billSearchResults.getBill();
            billDao.insert(parsedBill);

            // insert the bill action load
            BillActionLoad billActionLoad = BillActionLoad.create(parsedBill, billSearchResults.getUrl(), billSearchResults.getChecksum());
            billActionLoadDao.insert(billActionLoad);

            int sponsorsSaved = saveSponsors(connection, parsedBill, billActionLoad, billSearchResults);
            log.info("Saved sponsors: " + sponsorsSaved);

            BillActionLoad houseLoad = billSearchResults.createHouseBillActionLoad(parsedBill);
            billActionLoadDao.insert(houseLoad);
            int houseVotesSaved = saveVotes(connection, houseLoad, billSearchResults.getHouseVotes());
            log.info("Saved " + houseVotesSaved + " house votes");

            BillActionLoad senateLoad = billSearchResults.createSenateBillActionLoad(parsedBill);
            billActionLoadDao.insert(senateLoad);
            int senateVotesSaved = saveVotes(connection, senateLoad, billSearchResults.getSenateVotes());
            log.info("Saved " + senateVotesSaved + " senate votes");

            return parsedBill;
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

    private int saveSponsors(ConnectionWrapper connection, Bill bill, BillActionLoad billActionLoad, BillSearchResults billSearchResults){
        BillActionDao billActionDao = new BillActionDao(connection);

        SponsorNames sponsorNames = billSearchResults.getSponsorNames();

        int cnt = 0;
        if ( sponsorNames.getChiefHouseSponsor() != null ){
            saveSponsor(billActionDao, sponsorNames.getChiefHouseSponsor(), bill, billActionLoad, BillActionType.CHIEF_SPONSOR);
        }
        if ( sponsorNames.getChiefSenateSponsor() != null ){
            saveSponsor(billActionDao, sponsorNames.getChiefSenateSponsor(), bill, billActionLoad, BillActionType.CHIEF_SPONSOR);
        }
        for(SponsorName sponsorName : sponsorNames.getHouseSponsors()){
            saveSponsor(billActionDao, sponsorName, bill, billActionLoad, BillActionType.SPONSOR);
        }
        for(SponsorName sponsorName : sponsorNames.getSenateSponsors()){
            saveSponsor(billActionDao, sponsorName, bill, billActionLoad, BillActionType.SPONSOR);
        }


        return cnt;
    }

    private void saveSponsor(BillActionDao billActionDao, SponsorName sponsorName,
                             Bill bill, BillActionLoad billActionLoad, BillActionType billActionType){
        if( sponsorName.isComplete()) {
            BillAction billAction = new BillAction();
            billAction.setBill(bill);
            billAction.setLegislator(sponsorName.getLegislator());
            billAction.setBillActionLoad(billActionLoad);
            billAction.setBillActionType(billActionType);

            billActionDao.insert(billAction);
        }
    }
}
