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
import org.center4racialjustice.legup.domain.BillHistory;
import org.center4racialjustice.legup.domain.BillSaveResults;
import org.center4racialjustice.legup.domain.CompletedBillEvent;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.LegislatorBillAction;
import org.center4racialjustice.legup.illinois.BillIdentity;
import org.center4racialjustice.legup.util.LookupTable;
import org.center4racialjustice.legup.util.Tuple;

import java.sql.Connection;
import java.time.Instant;
import java.util.ArrayList;
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

    private BillSaveResults insertAllActions(Connection connection, Bill bill, BillStatusComputer billStatusComputer){
        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
        BillActionDao billActionDao = new BillActionDao(connection);

        BillActionLoad billActionLoad = billStatusComputer.getMainPageLoadRecord(bill);
        billActionLoadDao.insert(billActionLoad);

        List<BillAction> actions = billStatusComputer.allNonVoteActions(billActionLoad);
        actions.forEach(billAction -> billActionDao.insert(billAction));

        List<Tuple<CompletedBillEvent, BillActionLoad>> votesToInsert = billStatusComputer.allVoteActions(bill);

        List<BillActionLoad> loads = new ArrayList<>();
        loads.add(billActionLoad);

        for(Tuple<CompletedBillEvent, BillActionLoad> tuple : votesToInsert){
            BillActionLoad voteLoad = tuple.getSecond();
            billActionLoadDao.insert(voteLoad);
            BillAction billAction = billStatusComputer.voteActionToInsert(tuple.getFirst(), voteLoad);
            billActionDao.insert(billAction);
            loads.add(voteLoad);
        }
        return new BillSaveResults(bill, loads, actions);
    }

    private BillSaveResults insertNewActions(Connection connection, BillStatusComputer billStatusComputer){
        Bill bill = billStatusComputer.getPersistedBill();
        BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);
        BillActionDao billActionDao = new BillActionDao(connection);

        BillActionLoad billActionLoad = billStatusComputer.getMainPageLoadRecord(bill);
        billActionLoadDao.insert(billActionLoad);

        List<BillAction> actions = billStatusComputer.unpersistedNonVoteActions(billActionLoad);
        actions.forEach(billAction -> billActionDao.insert(billAction));

        List<Tuple<CompletedBillEvent, BillActionLoad>> votesToInsert = billStatusComputer.unpersistedVoteActions(bill);

        List<BillActionLoad> loads = new ArrayList<>();
        loads.add(billActionLoad);

        for(Tuple<CompletedBillEvent, BillActionLoad> tuple : votesToInsert){
            BillActionLoad voteLoad = tuple.getSecond();
            billActionLoadDao.insert(voteLoad);
            BillAction billAction = billStatusComputer.voteActionToInsert(tuple.getFirst(), voteLoad);
            billActionDao.insert(billAction);
            loads.add(voteLoad);
        }
        return new BillSaveResults(bill, loads, actions);
    }

    private BillSaveResults doFirstInsert(BillStatusComputer billStatusComputer){
        return connectionPool.runAndCommit(connection -> {
            Bill bill = insertNewBill(connection, billStatusComputer.getParsedBill());
            return insertAllActions(connection, bill, billStatusComputer);
        });
    }

    private BillSaveResults doForcedUpdate(BillStatusComputer billStatusComputer){
        Bill persistedBill = billStatusComputer.getPersistedBill();
        persistedBill.setShortDescription(billStatusComputer.getParsedBill().getShortDescription());

        return connectionPool.runAndCommit(connection -> {
            BillDao billDao = new BillDao(connection);
            billDao.update(persistedBill);
            deleteOldBillLoadsAndActions(connection, persistedBill);
            return insertAllActions(connection, persistedBill, billStatusComputer);
        });
    }

    public BillSaveResults saveParsedData(BillStatusComputer billStatusComputer, boolean forceSave) {

        log.info("Doing save of " + billStatusComputer.getBillIdentity() + " with force flag " + forceSave);

        if (!billStatusComputer.hasHistory()) {
            return doFirstInsert(billStatusComputer);
        }
        if ( forceSave ){
            return doForcedUpdate(billStatusComputer);
        }


        return connectionPool.useConnection(connection -> {
            return insertNewActions(connection, billStatusComputer);
        });
    }

    private void deleteOldBillLoadsAndActions(Connection connection, Bill bill){
        BillActionLoadDao loadDao = new BillActionLoadDao(connection);
        BillActionDao billActionDao = new BillActionDao(connection);

        List<BillAction> actions = billActionDao.readByBill(bill);
        List<BillActionLoad> loads = loadDao.readByBill(bill);

        actions.forEach(billActionDao::delete);
        loads.forEach(loadDao::delete);
    }

    private Bill insertNewBill(Connection connection, Bill bill) {
        BillDao billDao = new BillDao(connection);
        billDao.insert(bill);
        return bill;
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

}
