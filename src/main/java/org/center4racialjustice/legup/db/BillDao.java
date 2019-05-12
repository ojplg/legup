package org.center4racialjustice.legup.db;

import org.hrorm.Dao;
import org.center4racialjustice.legup.domain.Bill;

import java.sql.Connection;
import java.util.List;

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

    public List<Bill> readAll() {
        return innerDao.select();
    }
}
