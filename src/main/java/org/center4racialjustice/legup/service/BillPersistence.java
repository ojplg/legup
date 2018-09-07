package org.center4racialjustice.legup.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillActionLoadDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.illinois.BillHtmlParser;
import org.center4racialjustice.legup.illinois.BillVotes;
import org.center4racialjustice.legup.illinois.BillVotesParser;
import org.center4racialjustice.legup.illinois.CollatedVote;
import org.center4racialjustice.legup.illinois.SponsorNames;
import org.center4racialjustice.legup.illinois.VotesLegislatorsCollator;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BillPersistence {

    private final Logger log = LogManager.getLogger(BillPersistence.class);

    private final ConnectionPool connectionPool;

    public BillPersistence(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Bill saveParsedData(BillHtmlParser billHtmlParser, Map<String, String> votesMapUrl) throws IOException {

        try (ConnectionWrapper connection=connectionPool.getWrappedConnection()){
            BillDao billDao = new BillDao(connection);
            BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);

            Bill parsedBill = billHtmlParser.getBill();
            Bill dbBill = billDao.readBySessionChamberAndNumber(billHtmlParser.getSession(), billHtmlParser.getChamber(), billHtmlParser.getNumber());

            Bill bill;
            if ( dbBill != null ) {
                bill = dbBill;
                List<BillActionLoad> priorLoads = billActionLoadDao.readByBill(dbBill);
                List<BillActionLoad> loadsWithMatchingChecksums = priorLoads.stream()
                            .filter(load -> load.getCheckSum() == billHtmlParser.getChecksum())
                            .collect(Collectors.toList());

                // if this load has already been done and check-sums match-do nothing more
                if( loadsWithMatchingChecksums.size() > 0){
                    return dbBill;
                } else {
                    // FIXME
                    // data must be reloaded, though bill is presumably in good shape
                    // do deletes here?
                }

            } else {
                billDao.save(parsedBill);
                bill = parsedBill;
            }

            // insert the bill action load
            BillActionLoad billActionLoad = new BillActionLoad();
            billActionLoad.setBill(bill);
            billActionLoad.setUrl(billHtmlParser.getUrl());
            billActionLoad.setCheckSum(billHtmlParser.getChecksum());
            billActionLoad.setLoadTime(LocalDateTime.now());
            billActionLoadDao.insert(billActionLoad);

            LegislatorDao legislatorDao = new LegislatorDao(connection);
            List<Legislator> legislators = legislatorDao.readBySession(billHtmlParser.getSession());

            int sponsorsSaved = saveSponsors(connection, bill, billActionLoad, legislators, billHtmlParser);
            log.info("Saved sponsors: " + sponsorsSaved);

            int houseVotesSaved = saveVotes(connection, bill, billActionLoad, votesMapUrl, legislators, Chamber.House);
            log.info("Saved " + houseVotesSaved + " house votes");
            int senateVotesSaved = saveVotes(connection, bill, billActionLoad, votesMapUrl, legislators, Chamber.Senate);
            log.info("Saved " + senateVotesSaved + " senate votes");

            return bill;
        }
    }

    private int saveVotes(ConnectionWrapper connection, Bill bill, BillActionLoad billActionLoad, Map<String, String> votesMapUrl, List<Legislator> legislators, Chamber chamber) throws IOException {
        String votePdfUrl = null;
        for( Map.Entry<String,String> urlPair : votesMapUrl.entrySet()){
            if( urlPair.getKey().contains("Third Reading")
                    && urlPair.getValue().contains(chamber.getName().toLowerCase())){
                votePdfUrl = urlPair.getValue();
                break;
            }
        }
        if( votePdfUrl == null ){
            return 0;
        }

        BillVotes billVotes = BillVotesParser.readFromUrlAndParse(votePdfUrl);

        VotesLegislatorsCollator collator = new VotesLegislatorsCollator(legislators, billVotes);
        collator.collate();

        BillActionDao billActionDao = new BillActionDao(connection);

        int savedCount = 0;
        for (CollatedVote collatedVote : collator.getAllCollatedVotes()) {
            Vote vote = collatedVote.asVote(bill, billActionLoad);
            BillAction billAction = BillAction.fromVote(vote);
            billActionDao.insert(billAction);
            savedCount++;
        }
        return savedCount;
    }

    private int saveSponsors(ConnectionWrapper connection, Bill bill, BillActionLoad billActionLoad, List<Legislator> legislators, BillHtmlParser billHtmlParser){
        BillActionDao billActionDao = new BillActionDao(connection);

        SponsorNames sponsorNames = billHtmlParser.getSponsorNames();

        int cnt = 0;
        if ( sponsorNames.getHouseChiefSponsor() != null ){
            saveSponsor(billActionDao, sponsorNames.getHouseChiefSponsor(), legislators, bill, billActionLoad, BillActionType.CHIEF_SPONSOR);
        }
        if ( sponsorNames.getSenateChiefSponsor() != null ){
            saveSponsor(billActionDao, sponsorNames.getSenateChiefSponsor(), legislators, bill, billActionLoad, BillActionType.CHIEF_SPONSOR);
        }
        for(Tuple<String, String> tuple : sponsorNames.getHouseSponsors()){
            saveSponsor(billActionDao, tuple, legislators, bill, billActionLoad, BillActionType.SPONSOR);
        }
        for(Tuple<String, String> tuple : sponsorNames.getSenateSponsors()){
            saveSponsor(billActionDao, tuple, legislators, bill, billActionLoad, BillActionType.SPONSOR);
        }


        return cnt;
    }

    private void saveSponsor(BillActionDao billActionDao, Tuple<String, String> nameIdTuple, List<Legislator> legislators,
                             Bill bill, BillActionLoad billActionLoad, BillActionType billActionType){
        Legislator legislator = Lists.findfirst(legislators, l -> l.getMemberId().equals(nameIdTuple.getSecond()));

        if (legislator == null ){
            log.warn("Cannot find legislator for member id: " + nameIdTuple.getFirst() + "," + nameIdTuple.getSecond());
            return;
        }

        BillAction billAction = new BillAction();
        billAction.setBill(bill);
        billAction.setLegislator(legislator);
        billAction.setBillActionLoad(billActionLoad);
        billAction.setBillActionType(billActionType);

        billActionDao.insert(billAction);
    }
}
