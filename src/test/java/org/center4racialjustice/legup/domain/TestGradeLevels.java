package org.center4racialjustice.legup.domain;


import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestGradeLevels {

    @Test
    public void testGradeCalculation(){
        List<GradeLevel> levelList = new ArrayList<>();
        levelList.add(newLevel("A", 80L));
        levelList.add(newLevel("B", 60L));
        levelList.add(newLevel("C", 40L));
        levelList.add(newLevel("D", 20L));

        GradeLevels levels = new GradeLevels(levelList);

        
        String aGrade = levels.getGrade(85);
        Assert.assertEquals("A", aGrade);

        String dGrade = levels.getGrade(22);
        Assert.assertEquals("D", dGrade);

    }

    private GradeLevel newLevel(String grade, Long percentage){
        GradeLevel level = new GradeLevel();
        level.setGrade(grade);
        level.setPercentage(percentage);
        return level;
    }


}
