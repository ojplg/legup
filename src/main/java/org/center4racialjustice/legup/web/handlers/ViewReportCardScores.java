package org.center4racialjustice.legup.web.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.db.VoteDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.GradeCalculator;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.util.LookupTable;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.Util;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

public class ViewReportCardScores implements Handler {
    private static final Logger log = LogManager.getLogger(ViewReportCardScores.class);

    private final ConnectionPool connectionPool;

    public ViewReportCardScores(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException, SQLException {
        Connection connection = connectionPool.getConnection();
        try {
            VelocityContext velocityContext = new VelocityContext();

            Long reportCardId = Util.getLongParameter(request,"report_card_id");

            ReportCardDao reportCardDao = new ReportCardDao(connection);
            ReportCard reportCard = reportCardDao.read(reportCardId);

            long session = reportCard.getSessionNumber();

            LegislatorDao legislatorDao = new LegislatorDao(connection);
            List<Legislator> legislators = legislatorDao.readBySession(session);

            GradeCalculator calculator = new GradeCalculator(reportCard, legislators);
            List<Long> billIds = calculator.extractBillIds();

            BillDao billDao = new BillDao(connection);
            VoteDao voteDao = new VoteDao(connection);

            List<Bill> bills = billDao.readByIds(billIds);
            Map<Bill, List<Vote>> votesByBill = new HashMap<>();
            for(Bill bill : bills){
                List<Vote> votes = voteDao.readByBill(bill);
                votesByBill.put(bill, votes);
            }

            LookupTable<Legislator, Bill, Integer> scores = calculator.calculate(votesByBill);

            velocityContext.put("scores", scores);
            BinaryOperator<Integer> scoreComputer = (i, j) -> i.intValue() + j.intValue();
            velocityContext.put("computer", scoreComputer);

            return velocityContext;
        } finally {
            connection.close();
        }
    }
}
