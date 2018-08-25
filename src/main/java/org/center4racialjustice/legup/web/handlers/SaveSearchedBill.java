package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillActionLoadDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
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
import org.center4racialjustice.legup.illinois.VotesLegislatorsCollator;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SaveSearchedBill implements Handler {

    private final ConnectionPool connectionPool;

    public SaveSearchedBill(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException, SQLException {

        try (Connection connection = connectionPool.getConnection()) {

            HttpSession session = request.getSession();
            BillHtmlParser billHtmlParser = (BillHtmlParser) session.getAttribute("billHtmlParser");
            Map<String, String> votesMapUrl = (Map<String, String>) session.getAttribute("votesUrlsMap");

            BillDao billDao = new BillDao(connection);
            Bill bill = billHtmlParser.getBill();
            billDao.save(bill);

            BillActionLoad billActionLoad = new BillActionLoad();
            billActionLoad.setBill(bill);
            billActionLoad.setLoadTime(LocalDateTime.now());
            billActionLoad.setUrl(billHtmlParser.getUrl());
            billActionLoad.setCheckSum(0);

            BillActionLoadDao billActionLoadDao = new BillActionLoadDao(connection);

            billActionLoadDao.insert(billActionLoad);

            LegislatorDao legislatorDao = new LegislatorDao(connection);
            List<Legislator> legislators = legislatorDao.readBySession(billHtmlParser.getSession());


            int houseSponsorsSaved = saveSponsors(connection, bill, billActionLoad, legislators, billHtmlParser, Chamber.House);
            int senateSponsorsSaved = saveSponsors(connection, bill, billActionLoad, legislators, billHtmlParser, Chamber.Senate);

            int houseVotesSaved = saveVotes(connection, bill, billActionLoad, votesMapUrl, legislators, Chamber.House);
            int senateVotesSaved = saveVotes(connection, bill, billActionLoad, votesMapUrl, legislators, Chamber.Senate);

            VelocityContext velocityContext = new VelocityContext();

            velocityContext.put("bill", bill);
            velocityContext.put("house_sponsors_saved_count", houseSponsorsSaved);
            velocityContext.put("senate_sponsors_saved_count", senateSponsorsSaved);
            velocityContext.put("house_votes_saved_count", houseVotesSaved);
            velocityContext.put("senate_votes_saved_count", senateVotesSaved);

            return velocityContext;
        }
    }

    private int saveVotes(Connection connection, Bill bill, BillActionLoad billActionLoad, Map<String, String> votesMapUrl, List<Legislator> legislators, Chamber chamber) throws IOException {
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

    private int saveSponsors(Connection connection, Bill bill, BillActionLoad billActionLoad, List<Legislator> legislators, BillHtmlParser billHtmlParser, Chamber chamber){
        BillActionDao billActionDao = new BillActionDao(connection);

        List<Tuple<String, String>> tuples = billHtmlParser.getSponsorNames(chamber);

        int cnt = 0;
        for(Tuple<String, String> tuple : tuples){
            Legislator legislator = Lists.findfirst(legislators, l -> l.getMemberId().equals(tuple.getSecond()));
            
            // FIXME: this looks bogus
            if( legislator != null) {
                BillAction billAction = new BillAction();
                billAction.setBill(bill);
                billAction.setLegislator(legislator);
                billAction.setBillActionLoad(billActionLoad);
                billAction.setBillActionType(BillActionType.SPONSOR);

                billActionDao.insert(billAction);
                cnt++;
            }
        }
        return cnt;
    }
}
