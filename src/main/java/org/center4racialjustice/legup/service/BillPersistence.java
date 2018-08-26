package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.illinois.BillHtmlParser;

import java.sql.Connection;
import java.sql.SQLException;

public class BillPersistence {

    private final ConnectionPool connectionPool;

    public BillPersistence(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Bill saveParsedData(BillHtmlParser billHtmlParser) throws SQLException {

        try (Connection connection=connectionPool.getConnection()){

            // FIXME: This saves things twice.
            BillDao billDao = new BillDao(connection);
            Bill bill = billDao.findOrCreate(billHtmlParser.getSession(), billHtmlParser.getChamber(), billHtmlParser.getNumber());
            bill.setShortDescription(billHtmlParser.getShortDescription());
            billDao.save(bill);

            return bill;
        }
    }
}
