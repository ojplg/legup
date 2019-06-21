package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class GradeLevel implements Comparable<GradeLevel> {
    private Long id;
    private String grade;
    private long percentage;
    private Chamber chamber;

    public GradeLevel(){}

    public GradeLevel(Chamber chamber, String grade, long percentage){
        this.grade = grade;
        this.percentage = percentage;
        this.chamber = chamber;
    }

    @Override
    public int compareTo(GradeLevel o) {
        return (int) o.percentage - (int) this.percentage;
    }
}
