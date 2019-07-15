package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.LegislatorBillAction;
import org.hrorm.Dao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.Legislator;
import org.hrorm.Operator;

import java.sql.Connection;
import java.util.List;

import static org.hrorm.Operator.EQUALS;
import static org.hrorm.Where.inLong;
import static org.hrorm.Where.where;

public class BillActionDao {
    private final Dao<BillAction> innerDao;
    // FIXME: this kind of sucks
    private final Dao<LegislatorBillAction> legislatorBillActionDao;

    public BillActionDao(Connection connection) {

        this.innerDao = DaoBuilders.BILL_ACTIONS.buildDao(connection);
        this.legislatorBillActionDao = DaoBuilders.LEGISLATOR_BILL_ACTIONS.buildDao(connection);
    }

    public long insert(BillAction billAction){
        return innerDao.insert(billAction);
    }

    public List<BillAction> readByLegislator(Legislator legislator){
        List<Long> billActionIds = legislatorBillActionDao.selectDistinct("bill_action_id",
            where("legislator_id", EQUALS, legislator.getId()));

        return innerDao.select(inLong("ID", billActionIds));
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
