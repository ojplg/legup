package org.center4racialjustice.legup.domain;


import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestGradeLevels {

    @Test
    public void testGradeCalculation(){
        List<GradeLevel> levelList = new ArrayList<>();
        levelList.add(newLevel(Chamber.House,"A", 80L));
        levelList.add(newLevel(Chamber.House,"B", 60L));
        levelList.add(newLevel(Chamber.House,"C", 40L));
        levelList.add(newLevel(Chamber.House,"D", 20L));
        levelList.add(newLevel(Chamber.Senate,"A", 80L));
        levelList.add(newLevel(Chamber.Senate,"B", 60L));
        levelList.add(newLevel(Chamber.Senate,"C", 40L));
        levelList.add(newLevel(Chamber.Senate,"D", 20L));

        GradeLevels levels = new GradeLevels(levelList);

        String aGrade = levels.getGrade(Chamber.House, 85);
        Assert.assertEquals("A", aGrade);

        String dGrade = levels.getGrade(Chamber.House, 22);
        Assert.assertEquals("D", dGrade);

    }

    @Test
    public void testValidation_MissingLevel(){
        List<GradeLevel> levelList = new ArrayList<>();
        levelList.add(newLevel(Chamber.House,"A", 80L));
        levelList.add(newLevel(Chamber.House,"B", 60L));
        levelList.add(newLevel(Chamber.House,"D", 20L));
        levelList.add(newLevel(Chamber.Senate,"A", 80L));
        levelList.add(newLevel(Chamber.Senate,"B", 60L));
        levelList.add(newLevel(Chamber.Senate,"C", 40L));
        levelList.add(newLevel(Chamber.Senate,"D", 20L));

        try {
            GradeLevels levels = new GradeLevels(levelList);
            Assert.fail("Should not instantiate with missing level");
        } catch (Exception expected){
        }
    }

    @Test
    public void testValidation_OutOfOrder(){
        List<GradeLevel> levelList = new ArrayList<>();
        levelList.add(newLevel(Chamber.House,"A", 80L));
        levelList.add(newLevel(Chamber.House,"B", 40L));
        levelList.add(newLevel(Chamber.House,"C", 60L));
        levelList.add(newLevel(Chamber.House,"D", 20L));
        levelList.add(newLevel(Chamber.Senate,"A", 80L));
        levelList.add(newLevel(Chamber.Senate,"B", 60L));
        levelList.add(newLevel(Chamber.Senate,"C", 40L));
        levelList.add(newLevel(Chamber.Senate,"D", 20L));

        try {
            GradeLevels levels = new GradeLevels(levelList);
            Assert.fail("Should not instantiate out of order");
        } catch (Exception expected){
        }
    }


    private GradeLevel newLevel(Chamber chamber, String grade, Long percentage){
        GradeLevel level = new GradeLevel();
        level.setGrade(grade);
        level.setPercentage(percentage);
        level.setChamber(chamber);
        return level;
    }


}
