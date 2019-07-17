package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.illinois.BillIdentity;
import org.hrorm.Dao;
import org.center4racialjustice.legup.domain.Bill;
import org.hrorm.Where;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

import static org.hrorm.Where.where;
import static org.hrorm.Operator.EQUALS;

public class BillDao {

    private final Dao<Bill> innerDao;

    public BillDao(Connection connection) {
        this.innerDao = DaoBuilders.BILLS.buildDao(connection);
    }

    public Bill readBySessionChamberAndNumber(Bill bill){
        return innerDao.selectOne(bill, "SESSION_NUMBER", "CHAMBER", "BILL_NUMBER");
    }

    public long insert(Bill bill){
        return innerDao.insert(bill);
    }

    public List<Bill> readBySession(long session){
        Bill bill = new Bill();
        bill.setSession(session);

        return innerDao.select(bill, "SESSION_NUMBER");
    }

    public void update(Bill item) {
        innerDao.update(item);
    }

    public Bill read(long id) {
        return innerDao.selectOne(id);
    }

    public Bill read(BillIdentity billIdentity){
        return innerDao.selectOne(where("SESSION_NUMBER", EQUALS, billIdentity.getSession())
                .and("CHAMBER", EQUALS, billIdentity.getChamber().getName())
                .and("BILL_NUMBER", EQUALS, billIdentity.getNumber()));
    }

    public List<Bill> readAll() {
        return innerDao.select();
    }

    public List<Long> uniqueSessions(){
        List<Long> sessionNumbers = innerDao.selectDistinct("SESSION_NUMBER", new Where());
        Collections.sort(sessionNumbers);
        return sessionNumbers;
    }
}
