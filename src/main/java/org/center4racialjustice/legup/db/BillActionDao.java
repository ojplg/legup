package org.center4racialjustice.legup.db;

import org.hrorm.Dao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.Legislator;

import java.sql.Connection;
import java.util.List;

public class BillActionDao {
    private final Dao<BillAction> innerDao;

    public BillActionDao(ConnectionWrapper wrapper){
        this(wrapper.getConnection());
    }

    public BillActionDao(Connection connection) {
        this.innerDao = DaoBuilders.BILL_ACTIONS.buildDao(connection);
    }

    public long insert(BillAction billAction){
        return innerDao.insert(billAction);
    }

    public List<BillAction> readByLegislator(Legislator legislator){

        BillAction billAction = new BillAction();
        billAction.setLegislator(legislator);

        return innerDao.selectManyByColumns(billAction, "LEGISLATOR_ID");
    }

    public List<BillAction> readByBill(Bill bill){
        BillAction billAction = new BillAction();
        billAction.setBill(bill);

        return innerDao.selectManyByColumns(billAction, "BILL_ID");
    }

    public BillAction read(long id){
        return innerDao.select(id);
    }

}
