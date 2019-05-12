package org.center4racialjustice.legup.db;

import org.hrorm.Dao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;

import java.sql.Connection;
import java.util.List;

public class BillActionLoadDao {

    private final Dao<BillActionLoad> innerDao;

    public BillActionLoadDao(Connection connection) {
        this.innerDao = DaoBuilders.BILL_ACTION_LOADS.buildDao(connection);
    }

    public long insert(BillActionLoad billActionLoad) {
        return innerDao.insert(billActionLoad);
    }

    public void update(BillActionLoad billActionLoad){
        innerDao.update(billActionLoad);
    }


    public List<BillActionLoad> readByBill(Bill bill) {
        BillActionLoad billActionLoad = new BillActionLoad();
        billActionLoad.setBill(bill);
        return innerDao.select(billActionLoad, "BILL_ID");
    }

    public BillActionLoad select(Long id){
        return innerDao.selectOne(id);
    }

    public List<BillActionLoad> selectMany(List<Long> ids) {
        return innerDao.select(ids);
    }

    public List<BillActionLoad> selectAll() {
        return innerDao.select();
    }
}

