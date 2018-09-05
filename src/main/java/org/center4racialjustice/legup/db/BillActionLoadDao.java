package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.db.hrorm.Dao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class BillActionLoadDao {

    private final Dao<BillActionLoad> innerDao;

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
