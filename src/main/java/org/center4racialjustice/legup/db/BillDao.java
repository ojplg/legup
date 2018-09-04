package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.db.hrorm.DaoBuilder;
import org.center4racialjustice.legup.db.hrorm.Table;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.ChamberConverter;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class BillDao implements Dao<Bill> {

    public static String table = "bills";

    public static List<TypedColumn<Bill>> typedColumnList =
            Arrays.asList(
                    new LongColumn<>("ID", "", Bill::getId, Bill::setId),
                    new CodedEnumColumn<>("CHAMBER", "", Bill::getChamber, Bill::setChamber, ChamberConverter.INSTANCE),
                    new LongColumn<>("BILL_NUMBER", "", Bill::getNumber, Bill::setNumber),
                    new LongColumn<>("SESSION_NUMBER", "", Bill::getSession, Bill::setSession),
                    new StringColumn<>("SHORT_DESCRIPTION", "", Bill::getShortDescription, Bill::setShortDescription)
            );

    private static DaoBuilder<Bill> daoBuilder(){
        Table billTable = new Table("BILLS", Collections.emptyList());
        DaoBuilder<Bill> bldr = new DaoBuilder<>(billTable);
        bldr.withPrimaryKey("ID", Bill::getId, Bill::setId)
            .withSupplier(Bill::new)
            .withConvertingStringColumn("CHAMBER", Bill::getChamber, Bill::setChamber, Chamber.Converter)
            .withIntegerColumn("BILL_NUMBER", Bill::getNumber, Bill::setNumber)
            .withIntegerColumn("SESSION_NUMBER", Bill::getSession, Bill::setSession)
            .withStringColumn("SHORT_DESCRIPTION", Bill::getShortDescription, Bill::setShortDescription);
        return bldr;
    }

    public org.center4racialjustice.legup.db.hrorm.Dao<Bill> dao(Connection connection){
        DaoBuilder<Bill> bldr = daoBuilder();
        return bldr.buildDao(connection);
    }

    public static Supplier<Bill> supplier = Bill::new;

    private Connection connection;

    public BillDao(Connection connection) {
        this.connection = connection;
    }

    public Bill readBySessionChamberAndNumber(long session, Chamber chamber, long number){
        org.center4racialjustice.legup.db.hrorm.Dao<Bill> innerDao = dao(connection);

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

        org.center4racialjustice.legup.db.hrorm.Dao<Bill> innerDao = dao(connection);

        innerDao.insert(bill);
        return bill;
    }

    public List<Bill> readBySession(long session){
        org.center4racialjustice.legup.db.hrorm.Dao<Bill> innerDao = dao(connection);

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
        org.center4racialjustice.legup.db.hrorm.Dao<Bill> innerDao = dao(connection);
        return innerDao.insert(item);
    }

    @Override
    public Bill read(long id) {
        org.center4racialjustice.legup.db.hrorm.Dao<Bill> innerDao = dao(connection);
        return innerDao.select(id);
    }

    @Override
    public List<Bill> readAll() {
        org.center4racialjustice.legup.db.hrorm.Dao<Bill> innerDao = dao(connection);
        return innerDao.selectAll();
    }
}
