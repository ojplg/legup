package org.center4racialjustice.legup.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GradeLevels {

    public static final String[] REQUIRED_GRADES = { "A", "B", "C", "D" };

    public static final List<GradeLevel> DEFAULTS = Arrays.asList(
            new GradeLevel( "A", 80),
            new GradeLevel( "B", 60),
            new GradeLevel( "C", 40),
            new GradeLevel( "D", 20)
    );

    private final List<GradeLevel> levels;

    public GradeLevels(List<GradeLevel> levelList){
        ArrayList<GradeLevel> list = new ArrayList<>(levelList);
        Collections.sort(list);
        levels = Collections.unmodifiableList(list);
        validate();
    }

    public String getGrade(int percentage){
        for( GradeLevel level : levels ){
            if ( percentage > level.getPercentage() ){
                return level.getGrade();
            }
        }
        return "F";
    }

    public long getPercentage(String grade){
        for(GradeLevel level : levels){
            if ( level.getGrade().equals(grade)){
                return level.getPercentage();
            }
        }
        throw new IllegalArgumentException("No percentage for " + grade);
    }

    private void validate(){
        if (levels.size() != REQUIRED_GRADES.length ){
            throw new RuntimeException("Wrong number of grade levels " + levels);
        }

        for(int idx=0; idx<REQUIRED_GRADES.length; idx++){
            String required = REQUIRED_GRADES[idx];
            GradeLevel level = levels.get(idx);
            if ( ! level.getGrade().equals(required) ){
                throw new RuntimeException("Missing grade: " + required + " from " + levels);
            }
        }
    }

}
