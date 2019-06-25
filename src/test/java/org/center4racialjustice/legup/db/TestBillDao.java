package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.illinois.LegislationType;
import org.junit.Assert;
import org.junit.Test;

public class TestBillDao {

    static {
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }

    @Test
    public void testSearchByChamberStuff(){

        BillDao billDao = new BillDao(DbTestConfigs.connect());

        Bill insertBill = new Bill();
        insertBill.setSession(100L);
        insertBill.setChamber(Chamber.House);
        insertBill.setNumber(2771L);
        insertBill.setShortDescription("Foo");
        insertBill.setLegislationSubType(LegislationType.BILL_SUB_TYPE);

        billDao.insert(insertBill);

        Assert.assertNotNull(insertBill.getId());

        Bill templateBill = new Bill();
        templateBill.setSession(100L);
        templateBill.setChamber(Chamber.House);
        templateBill.setNumber(2771L);

        Bill readBill = billDao.readBySessionChamberAndNumber(templateBill);

        Assert.assertNotNull(readBill);
    }

}
