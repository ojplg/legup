package org.center4racialjustice.legup.db;

import org.hrorm.Schema;
import org.junit.Assert;
import org.junit.Test;

public class TestDaoBuilders {

    @Test
    public void committeeSchemas(){
        Schema committeeMemberSchema = new Schema(DaoBuilders.COMMITTEE_MEMBERS);
        Schema committeeSchema = new Schema(DaoBuilders.COMMITTEE);

        Assert.assertNotNull(committeeMemberSchema);
        Assert.assertNotNull(committeeSchema);

//        System.out.println(committeeMemberSchema.sql());
//        System.out.println(committeeSchema.sql());
    }

}
