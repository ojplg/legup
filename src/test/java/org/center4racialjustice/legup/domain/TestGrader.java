package org.center4racialjustice.legup.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class TestGrader {

    @Test
    public void testHandlesEmptyList(){
        GradeLevels levels = new GradeLevels(GradeLevels.DEFAULTS);

        Grader grader = new Grader(levels, Collections.emptyList());

        Assert.assertEquals(0, grader.getMean());

    }
}
