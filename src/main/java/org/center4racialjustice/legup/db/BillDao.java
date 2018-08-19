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
                    new LongColumn<>("BILL_NUMBER", "", Bill::getNumber, Bill::setNumber)
            );

    public static Supplier<Bill> supplier = () -> new Bill();

    public BillDao(Connection connection) {
        super(connection, supplier, table, typedColumnList);
    }

    public Bill readByChamberAndNumber(Chamber chamber, long number){
        StringBuilder sqlBldr = new StringBuilder();
        sqlBldr.append(DaoHelper.selectString(table, typedColumnList));
        sqlBldr.append(" where chamber = '");
        sqlBldr.append(chamber.toString());
        sqlBldr.append("' and bill_number = ");
        sqlBldr.append(number);
        String sql = sqlBldr.toString();

        List<Bill> bills = DaoHelper.read(connection, sql, typedColumnList, supplier);
        return DaoHelper.fromSingletonList(bills, "Searching for bill by chamber and number.");
    }

    public Bill findOrCreate(Chamber chamber, long number){
        Bill found = readByChamberAndNumber(chamber, number);
        if( found != null ){
            return found;
        }
        Bill bill = new Bill();
        bill.setChamber(chamber);
        bill.setNumber(number);
        Long id = save(bill);
        bill.setId(id);
        return bill;
    }
}
