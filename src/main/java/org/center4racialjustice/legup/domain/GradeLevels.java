package org.center4racialjustice.legup.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GradeLevels {

    private static final String[] REQUIRED_GRADES = { "A", "B", "C", "D" };

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
