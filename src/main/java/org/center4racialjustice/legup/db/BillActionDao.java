package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.Legislator;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class BillActionDao {
    private final org.center4racialjustice.legup.db.hrorm.Dao<BillAction> innerDao;

    public final Supplier<BillAction> supplier = BillAction::new;

    public BillActionDao(Connection connection) {
        this.innerDao = DaoBuilders.BILL_ACTIONS.buildDao(connection);
    }

    public long insert(BillAction billAction){
        return innerDao.insert(billAction);
    }

    public List<BillAction> readByLegislator(Legislator legislator){

        BillAction billAction = new BillAction();
        billAction.setLegislator(legislator);

        return innerDao.selectManyByColumns(billAction, Arrays.asList("LEGISLATOR_ID"));
    }

    public List<BillAction> readByBill(Bill bill){
        BillAction billAction = new BillAction();
        billAction.setBill(bill);

        return innerDao.selectManyByColumns(billAction, Arrays.asList("BILL_ID"));
    }

    public BillAction read(long id){
        return innerDao.select(id);
    }

}
