package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class GradeLevel implements Comparable<GradeLevel> {
    private Long id;
    private String grade;
    private long percentage;

    public GradeLevel(){}

    public GradeLevel(String grade, long percentage){
        this.grade = grade;
        this.percentage = percentage;
    }

    @Override
    public int compareTo(GradeLevel o) {
        return (int) o.percentage - (int) this.percentage;
    }
}
