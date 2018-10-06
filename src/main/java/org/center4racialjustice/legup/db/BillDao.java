package org.center4racialjustice.legup.db;

import org.hrorm.Dao;
import org.center4racialjustice.legup.domain.Bill;

import java.sql.Connection;
import java.util.List;

public class BillDao {

    private final Dao<Bill> innerDao;

    public BillDao(ConnectionWrapper wrapper) {
        this(wrapper.getConnection());
    }

    public BillDao(Connection connection) {
        this.innerDao = DaoBuilders.BILLS.buildDao(connection);
    }

    public Bill readBySessionChamberAndNumber(Bill bill){
        return innerDao.selectByColumns(bill, "SESSION_NUMBER", "CHAMBER", "BILL_NUMBER");
    }

    public void insert(Bill bill){
        innerDao.insert(bill);
    }

    public List<Bill> readBySession(long session){
        Bill bill = new Bill();
        bill.setSession(session);

        return innerDao.selectManyByColumns(bill, "SESSION_NUMBER");
    }

    public List<Bill> readByIds(List<Long> ids){
        return innerDao.selectMany(ids);
    }

    public long save(Bill item) {
        return innerDao.insert(item);
    }

    public Bill read(long id) {
        return innerDao.select(id);
    }

    public List<Bill> readAll() {
        return innerDao.selectAll();
    }
}
