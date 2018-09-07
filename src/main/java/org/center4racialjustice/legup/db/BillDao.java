package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.db.hrorm.Dao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class BillDao {

    private final Dao<Bill> innerDao;

    public BillDao(ConnectionWrapper wrapper) {
        this(wrapper.getConnection());
    }

    public BillDao(Connection connection) {
        this.innerDao = DaoBuilders.BILLS.buildDao(connection);
    }

    public Bill readBySessionChamberAndNumber(long session, Chamber chamber, long number){
        Bill bill = new Bill();
        bill.setSession(session);
        bill.setChamber(chamber);
        bill.setNumber(number);

        return innerDao.selectByColumns(bill, Arrays.asList("SESSION_NUMBER", "CHAMBER", "BILL_NUMBER"));
    }

    public Bill findOrCreate(long session, Chamber chamber, long number){
        Bill found = readBySessionChamberAndNumber(session, chamber, number);
        if( found != null ){
            return found;
        }
        Bill bill = new Bill();
        bill.setChamber(chamber);
        bill.setNumber(number);
        bill.setSession(session);

        innerDao.insert(bill);
        return bill;
    }

    public List<Bill> readBySession(long session){
        Bill bill = new Bill();
        bill.setSession(session);

        return innerDao.selectManyByColumns(bill, Arrays.asList("SESSION_NUMBER"));
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
