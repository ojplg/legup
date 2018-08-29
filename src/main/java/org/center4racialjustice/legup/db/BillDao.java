package org.center4racialjustice.legup.db;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.ChamberConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class BillDao {

    private static Logger log = LogManager.getLogger(BillDao.class);

    public static String table = "bills";

    public static List<TypedColumn<Bill>> typedColumnList =
            Arrays.asList(
                    new LongColumn<>("ID", "", Bill::getId, Bill::setId),
                    new CodedEnumColumn<>("CHAMBER", "", Bill::getChamber, Bill::setChamber, ChamberConverter.INSTANCE),
                    new LongColumn<>("BILL_NUMBER", "", Bill::getNumber, Bill::setNumber),
                    new LongColumn<>("SESSION_NUMBER", "", Bill::getSession, Bill::setSession),
                    new StringColumn<>("SHORT_DESCRIPTION", "", Bill::getShortDescription, Bill::setShortDescription)
            );

    public static Supplier<Bill> supplier = Bill::new;

    private final BillMapper billMapper;
    private final SqlSession sqlSession;

    public BillDao(SqlSession sqlSession){
        this.billMapper = sqlSession.getMapper(BillMapper.class);
        this.sqlSession = sqlSession;
    }

//    public BillDao(BillMapper billMapper) {
//        this.billMapper = billMapper;
//    }

    public Bill readBySessionChamberAndNumber(long session, Chamber chamber, long number){
        return billMapper.selectBillBySessionChamberAndNumber(session, chamber, number);
    }

    public Bill findOrCreate(long session, Chamber chamber, long number){
        Bill found = billMapper.selectBillBySessionChamberAndNumber(session, chamber, number);
        if( found != null ){
            return found;
        }
        Bill bill = new Bill();
        bill.setChamber(chamber);
        bill.setNumber(number);
        bill.setSession(session);
        billMapper.insert(bill);

        return billMapper.selectBillBySessionChamberAndNumber(session, chamber, number);
    }

    public long save(Bill bill){
        billMapper.insert(bill);
        sqlSession.commit();
        return bill.getId();
    }

    public Bill read(long id){
        return billMapper.selectBill(id);
    }

    public List<Bill> readAll(){
        return billMapper.selectBills();
    }


    public List<Bill> readBySession(long session){
        return billMapper.selectBillsBySession(session);
    }

    public List<Bill> readByIds(List<Long> ids){
        if( ids == null ){
            return Collections.emptyList();
        }
        StringBuilder bldr = new StringBuilder();
        bldr.append("(");
        for(int idx=0; idx<ids.size(); idx++){
            bldr.append(ids.get(idx));
            if( idx < ids.size() -1 ){
                bldr.append(", ");
            }
        }
        bldr.append(")");
        log.info(" SEARCHING BY IDS  " + bldr.toString());
        return billMapper.selectByIds(bldr.toString());
    }
}
