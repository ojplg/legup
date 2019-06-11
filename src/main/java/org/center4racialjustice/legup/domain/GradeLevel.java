package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class GradeLevel implements Comparable<GradeLevel> {
    private Long id;
    private String grade;
    private long percentage;

    @Override
    public int compareTo(GradeLevel o) {
        return (int) o.percentage - (int) this.percentage;
    }
}
