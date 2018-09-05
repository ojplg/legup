package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class BillActionLoadDao {

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
    private final org.center4racialjustice.legup.db.hrorm.Dao<BillActionLoad> innerDao;

    public static final Supplier<BillActionLoad> supplier = BillActionLoad::new;

    public BillActionLoadDao(Connection connection) {
        this.connection = connection;
        this.innerDao = DaoBuilders.BILL_ACTION_LOADS.buildDao(connection);
    }

    public long insert(BillActionLoad billActionLoad){
        return innerDao.insert(billActionLoad);
    }

    public List<BillActionLoad> readByBill(Bill bill){
        BillActionLoad billActionLoad = new BillActionLoad();
        billActionLoad.setBill(bill);
        return innerDao.selectManyByColumns(billActionLoad, Arrays.asList( "BILL_ID" ));
    }

}
