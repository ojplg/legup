package org.center4racialjustice.legup.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GradeLevels {

    public static final String[] REQUIRED_GRADES = { "A", "B", "C", "D" };

    public static final List<GradeLevel> DEFAULTS = Arrays.asList(
            new GradeLevel(Chamber.House, "A", 80),
            new GradeLevel(Chamber.House, "B", 60),
            new GradeLevel(Chamber.House, "C", 40),
            new GradeLevel(Chamber.House, "D", 20),
            new GradeLevel(Chamber.Senate, "A", 80),
            new GradeLevel(Chamber.Senate, "B", 60),
            new GradeLevel(Chamber.Senate, "C", 40),
            new GradeLevel(Chamber.Senate, "D", 20)
    );

    private final List<GradeLevel> houseLevels;
    private final List<GradeLevel> senateLevels;

    public GradeLevels(List<GradeLevel> levelList){
        houseLevels = orderedLevels(levelList, Chamber.House);
        senateLevels = orderedLevels(levelList, Chamber.Senate);
        validate(houseLevels);
        validate(senateLevels);
    }

    private List<GradeLevel> orderedLevels(List<GradeLevel> levelList, Chamber chamber){
        List<GradeLevel> list = levelList.stream().filter(gl->gl.getChamber().equals(chamber)).collect(Collectors.toList());
        Collections.sort(list);
        return Collections.unmodifiableList(list);
    }

    public String getGrade(Chamber chamber, int percentage){
        List<GradeLevel> levels = chamber.equals(Chamber.House) ? houseLevels : senateLevels;
        for( GradeLevel level : levels ){
            if ( percentage > level.getPercentage() ){
                return level.getGrade();
            }
        }
        return "F";
    }

    public long getPercentage(Chamber chamber, String grade){
        List<GradeLevel> levels = chamber.equals(Chamber.House) ? houseLevels : senateLevels;
        for(GradeLevel level : levels){
            if ( level.getGrade().equals(grade)){
                return level.getPercentage();
            }
        }
        throw new IllegalArgumentException("No percentage for " + grade);
    }

    public long getPercentage(String chamberString, String grade){
        return getPercentage(Chamber.fromString(chamberString), grade);
    }

    private void validate(List<GradeLevel> levels){
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
