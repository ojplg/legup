package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class Grade implements Comparable<Grade> {
    private final int percentage;
    private final String letter;

    public Grade(int percentage){
        if( percentage < 0 || percentage > 100){
            throw new RuntimeException("Percentage not between 0 and 100: " + percentage);
        }
        this.percentage = percentage;
        if( percentage > 80 ){
            letter = "A";
        } else if (percentage > 60){
            letter = "B";
        } else if (percentage > 40){
            letter = "C";
        } else if (percentage > 20){
            letter = "D";
        } else {
            letter = "F";
        }
    }

    @Override
    public int compareTo(Grade o) {
        return this.percentage - o.percentage;
    }
}
