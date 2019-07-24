package org.center4racialjustice.legup.db;

import org.hrorm.Dao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;

import java.sql.Connection;
import java.util.List;

import static org.hrorm.Operator.EQUALS;
import static org.hrorm.Where.where;

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
        return readByBillId(bill.getId());
    }

    public List<BillActionLoad> readByBillId(long billId){
        return innerDao.select(where("BILL_ID", EQUALS, billId));
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

    public void delete(BillActionLoad billActionLoad) { innerDao.delete(billActionLoad);}
}

