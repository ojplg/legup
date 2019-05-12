package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.BillActionLoad;
import org.hrorm.Dao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.Legislator;

import java.sql.Connection;
import java.util.List;

public class BillActionDao {
    private final Dao<BillAction> innerDao;

    public BillActionDao(Connection connection) {
        this.innerDao = DaoBuilders.BILL_ACTIONS.buildDao(connection);
    }

    public long insert(BillAction billAction){
        return innerDao.insert(billAction);
    }

    public List<BillAction> readByLegislator(Legislator legislator){

        BillAction billAction = new BillAction();
        billAction.setLegislator(legislator);

        return innerDao.select(billAction, "LEGISLATOR_ID");
    }

    public List<BillAction> readByBill(Bill bill){
        BillAction billAction = new BillAction();
        billAction.setBill(bill);

        return innerDao.select(billAction, "BILL_ID");
    }

    public List<BillAction> readByBillActionLoad(BillActionLoad billActionLoad){
        BillAction billAction = new BillAction();
        billAction.setBillActionLoad(billActionLoad);

        return innerDao.select(billAction, "BILL_ACTION_LOAD_ID");
    }

    public BillAction read(long id){
        return innerDao.selectOne(id);
    }

    public void delete(BillAction billAction){
        innerDao.delete(billAction);
    }

}
