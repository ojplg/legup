package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.illinois.BillHtmlParser;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class SaveSearchedBill implements Handler {

    private final ConnectionPool connectionPool;

    public SaveSearchedBill(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException {

        BillPersistence billPersistence = new BillPersistence(connectionPool);

        HttpSession session = request.getSession();
        BillHtmlParser billHtmlParser = (BillHtmlParser) session.getAttribute("billHtmlParser");
        Map<String, String> votesMapUrl = (Map<String, String>) session.getAttribute("votesUrlsMap");

        Bill bill = billPersistence.saveParsedData(billHtmlParser, votesMapUrl);

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("bill", bill);
        return velocityContext;
    }
}
