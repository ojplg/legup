package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class BillActionLoadDao {

    private final org.center4racialjustice.legup.db.hrorm.Dao<BillActionLoad> innerDao;

    public static final Supplier<BillActionLoad> supplier = BillActionLoad::new;

    public BillActionLoadDao(Connection connection) {
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
