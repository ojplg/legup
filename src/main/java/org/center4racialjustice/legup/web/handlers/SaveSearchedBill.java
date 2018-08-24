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
import org.center4racialjustice.legup.illinois.BillHtmlParser;
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

public class SaveSearchedBill implements Handler {

    private final ConnectionPool connectionPool;

    public SaveSearchedBill(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws SQLException {

        try (Connection connection = connectionPool.getConnection()) {

            HttpSession session = request.getSession();
            BillHtmlParser billHtmlParser = (BillHtmlParser) session.getAttribute("billHtmlParser");

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

            int houseSponsorsSaved = saveSponsors(connection, bill, billActionLoad, billHtmlParser, Chamber.House);
            int senateSponsorsSaved = saveSponsors(connection, bill, billActionLoad, billHtmlParser, Chamber.Senate);

            VelocityContext velocityContext = new VelocityContext();

            velocityContext.put("bill", bill);
            velocityContext.put("house_sponsors_saved_count", houseSponsorsSaved);
            velocityContext.put("senate_sponsors_saved_count", senateSponsorsSaved);

            return velocityContext;
        }
    }

    private int saveSponsors(Connection connection, Bill bill, BillActionLoad billActionLoad, BillHtmlParser billHtmlParser, Chamber chamber){
        BillActionDao billActionDao = new BillActionDao(connection);
        LegislatorDao legislatorDao = new LegislatorDao(connection);

        List<Legislator> legislators = legislatorDao.readBySession(billHtmlParser.getSession());

        List<Tuple<String, String>> tuples = billHtmlParser.getSponsorNames(chamber);

        int cnt = 0;
        for(Tuple<String, String> tuple : tuples){
            Legislator legislator = Lists.findfirst(legislators, l -> l.getMemberId().equals(tuple.getSecond()));

            BillAction billAction = new BillAction();
            billAction.setBill(bill);
            billAction.setLegislator(legislator);
            billAction.setBillActionLoad(billActionLoad);
            billAction.setBillActionType(BillActionType.SPONSOR);

            billActionDao.insert(billAction);
            cnt++;
        }
        return cnt;
    }
}
