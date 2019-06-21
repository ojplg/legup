package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class Grade implements Comparable<Grade> {
    private final int percentage;
    private final String letter;

    public Grade(int percentage, Chamber chamber, GradeLevels levels){
        if( percentage < 0 || percentage > 100){
            throw new RuntimeException("Percentage not between 0 and 100: " + percentage);
        }
        this.percentage = percentage;
        this.letter = levels.getGrade(chamber, percentage);
    }

    @Override
    public int compareTo(Grade o) {
        return this.percentage - o.percentage;
    }
}
