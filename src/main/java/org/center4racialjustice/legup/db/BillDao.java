package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class BillDao implements Dao<Bill> {

    public org.center4racialjustice.legup.db.hrorm.Dao<Bill> dao(Connection connection){
        return DaoBuilders.BILLS.buildDao(connection);
    }

    public static Supplier<Bill> supplier = Bill::new;

    private Connection connection;
    private final org.center4racialjustice.legup.db.hrorm.Dao<Bill> innerDao;

    public BillDao(Connection connection) {
        this.connection = connection;
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
        org.center4racialjustice.legup.db.hrorm.Dao<Bill> innerDao = dao(connection);
        return innerDao.selectMany(ids);
    }

    @Override
    public long save(Bill item) {
        return innerDao.insert(item);
    }

    @Override
    public Bill read(long id) {
        return innerDao.select(id);
    }

    @Override
    public List<Bill> readAll() {
        return innerDao.selectAll();
    }
}
