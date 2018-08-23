package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.ChamberConverter;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class BillDao extends OneTableDao<Bill> {

    public static String table = "bills";

    public static List<TypedColumn<Bill>> typedColumnList =
            Arrays.asList(
                    new LongColumn<>("ID", "", Bill::getId, Bill::setId),
                    new CodedEnumColumn<>("CHAMBER", "", Bill::getChamber, Bill::setChamber, ChamberConverter.INSTANCE),
                    new LongColumn<>("BILL_NUMBER", "", Bill::getNumber, Bill::setNumber),
                    new LongColumn<>("SESSION_NUMBER", "", Bill::getSession, Bill::setSession),
                    new StringColumn<>("SHORT_DESCRIPTION", "", Bill::getShortDescription, Bill::setShortDescription)
            );

    public static Supplier<Bill> supplier = () -> new Bill();

    public BillDao(Connection connection) {
        super(connection, supplier, table, typedColumnList);
    }

    public Bill readBySessionChamberAndNumber(long session, Chamber chamber, long number){
        StringBuilder sqlBldr = new StringBuilder();
        sqlBldr.append(DaoHelper.selectString(table, typedColumnList));
        sqlBldr.append(" where chamber = '");
        sqlBldr.append(chamber.toString());
        sqlBldr.append("' and bill_number = ");
        sqlBldr.append(number);
        sqlBldr.append(" and session_number = ");
        sqlBldr.append(session);
        String sql = sqlBldr.toString();

        List<Bill> bills = DaoHelper.read(connection, sql, typedColumnList, supplier);
        return DaoHelper.fromSingletonList(bills, "Searching for bill by chamber and number.");
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
        Long id = save(bill);
        bill.setId(id);
        return bill;
    }

    public List<Bill> readBySession(long session){
        StringBuilder sqlBldr = new StringBuilder();
        sqlBldr.append(DaoHelper.selectString(table, typedColumnList));
        sqlBldr.append(" where session_number = ");
        sqlBldr.append(session);
        String sql = sqlBldr.toString();

        return DaoHelper.read(connection, sql, typedColumnList, supplier);
    }

    public List<Bill> readByIds(List<Long> ids){
        return DaoHelper.read(connection, table,  typedColumnList, ids, supplier);
    }
}
