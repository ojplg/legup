package org.center4racialjustice.legup.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class BillActionLoadDao {

    private static final Logger log = LogManager.getLogger(BillActionDao.class);

    public static final String table = "BILL_ACTION_LOADS";

    public static final List<TypedColumn<BillActionLoad>> dataColumns =
            Arrays.asList(
                    new LongColumn<>("ID", "a", BillActionLoad::getId, BillActionLoad::setId),
                    new LocalDateTimeColumn<>("LOAD_TIME", "a", BillActionLoad::getLoadTime, BillActionLoad::setLoadTime),
                    new StringColumn<>("URL", "a", BillActionLoad::getUrl, BillActionLoad::setUrl),
                    new LongColumn<>("CHECK_SUM", "a", BillActionLoad::getCheckSum, BillActionLoad::setCheckSum)
            );

    public final JoinColumn<BillActionLoad,Bill> billColumn =
            new JoinColumn<>("BILL_ID", "b", "bills", BillActionLoad::getBill, BillActionLoad::setBill,
                    BillDao.supplier, BillDao.typedColumnList );

    private final List<JoinColumn<BillActionLoad, ?>> joinColumns =
            Arrays.asList( billColumn );

    private final Connection connection;

    public static final Supplier<BillActionLoad> supplier = () -> new BillActionLoad();

    public BillActionLoadDao(Connection connection) {
        this.connection = connection;
    }

    public long insert(BillActionLoad billActionLoad){
        return DaoHelper.doInsert(connection, table, dataColumns, joinColumns, billActionLoad);
    }

    public List<BillActionLoad> readByBill(Bill bill){
        StringBuilder buf = new StringBuilder();

        buf.append(DaoHelper.selectString(table, dataColumns));

        buf.append(" and a.bill_id = ");
        buf.append(bill.getId());
        String sql = buf.toString();

        List<BillActionLoad> votes = DaoHelper.doSelect(connection, sql, supplier, dataColumns);

        votes.forEach(v -> v.setBill(bill));

        return votes;
    }

}
