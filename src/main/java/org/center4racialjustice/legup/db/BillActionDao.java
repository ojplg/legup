package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.Legislator;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class BillActionDao {

    private final String table = "BILL_ACTIONS";

    private final List<TypedColumn<BillAction>> dataColumns =
            Arrays.asList(
                    new LongColumn<>("ID", "a", BillAction::getId, BillAction::setId),
                    new CodedEnumColumn<>("BILL_ACTION_TYPE", "a", BillAction::getBillActionType, BillAction::setBillActionType, BillActionType.CONVERTER),
                    new StringColumn<>("BILL_ACTION_DETAIL", "a", BillAction::getBillActionDetail, BillAction::setBillActionDetail)
            );

    private final JoinColumn<BillAction,Bill> billColumn =
            new JoinColumn<>("BILL_ID", "b", "bills", BillAction::getBill, BillAction::setBill,
                    BillDao.supplier, BillDao.typedColumnList );

    private final JoinColumn<BillAction,Legislator> legislatorColumn =
            new JoinColumn<>("LEGISLATOR_ID", "c", "legislators", BillAction::getLegislator, BillAction::setLegislator,
                    LegislatorDao.supplier, LegislatorDao.typedColumnList );

    private final JoinColumn<BillAction,BillActionLoad> voteLoadColumn =
            new JoinColumn<>("BILL_ACTION_LOAD_ID", "d", BillActionLoadDao.table, BillAction::getBillActionLoad, BillAction::setBillActionLoad,
                    BillActionLoadDao.supplier, BillActionLoadDao.dataColumns );

    private final List<JoinColumn<BillAction, ?>> joinColumns =
            Arrays.asList( billColumn, legislatorColumn, voteLoadColumn );

    private final Connection connection;
    private final org.center4racialjustice.legup.db.hrorm.Dao<BillAction> innerDao;

    public final Supplier<BillAction> supplier = BillAction::new;

    public BillActionDao(Connection connection) {
        this.connection = connection;
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
