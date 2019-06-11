package org.center4racialjustice.legup.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GradeLevels {

    private static final List<String> REQUIRED_GRADES = Arrays.asList("A", "B", "C", "D" );

    private final List<GradeLevel> levels = new ArrayList<>();

    public GradeLevels(List<GradeLevel> levelList){

        if (levelList.size() != 4 ){
            throw new RuntimeException("Wrong number of grade levels " + levelList);
        }

        Set<String> required = new HashSet<>(REQUIRED_GRADES);

        for(GradeLevel level : levelList ){
            this.levels.add(level);
            required.remove(level.getGrade());
        }

        if( required.size() > 0 ){
            throw new RuntimeException("No level set for some grades: " + required);
        }

        Collections.sort(levels);
    }

    public String getGrade(int percentage){
        for( GradeLevel level : levels ){
            if ( percentage > level.getPercentage() ){
                return level.getGrade();
            }
        }
        return "F";
    }
}
