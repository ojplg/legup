package org.center4racialjustice.legup.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger log = LogManager.getLogger(BillActionDao.class);

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

    public final Supplier<BillAction> supplier = () -> new BillAction();

    public BillActionDao(Connection connection) {
        this.connection = connection;
    }

    public long insert(BillAction billAction){
        return DaoHelper.doInsert(connection, table, dataColumns, joinColumns, billAction);
    }

    public List<BillAction> readByLegislator(Legislator legislator){
        StringBuilder buf = new StringBuilder();

        buf.append(DaoHelper.joinSelectSql(table, dataColumns, Collections.singletonList(billColumn)));

        buf.append(" and a.legislator_id = ");
        buf.append(legislator.getId());
        String sql = buf.toString();

        List<BillAction> actions = DaoHelper.doSelect(connection, sql, supplier, dataColumns, Collections.singletonList(billColumn));

        actions.forEach(a -> a.setLegislator(legislator));

        return actions;

    }

    public List<BillAction> readByBill(Bill bill){
        StringBuilder buf = new StringBuilder();

        buf.append(DaoHelper.joinSelectSql(table, dataColumns, Collections.singletonList(legislatorColumn)));

        buf.append(" and a.bill_id = ");
        buf.append(bill.getId());
        String sql = buf.toString();

        List<BillAction> actions = DaoHelper.doSelect(connection, sql, supplier, dataColumns, Collections.singletonList(legislatorColumn));

        actions.forEach(a -> a.setBill(bill));

        return actions;
    }

    public BillAction read(long id){

        StringBuilder buf = new StringBuilder();

        buf.append(DaoHelper.joinSelectSql(table, dataColumns, joinColumns));

        buf.append(" and a.id = ");
        buf.append(id);

        String sql = buf.toString();

        List<BillAction> actions = DaoHelper.doSelect(connection, sql, supplier, dataColumns, joinColumns);

        return DaoHelper.fromSingletonList(actions, "Reading bill action");
    }

}
