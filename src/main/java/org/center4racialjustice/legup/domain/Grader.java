package org.center4racialjustice.legup.domain;

import java.util.Collection;

public class Grader {

    private final int lowScore;
    private final int highScore;
    private final int spread;
    private final int mean;

    private final GradeLevels gradeLevels;

    public Grader(GradeLevels gradeLevels, Collection<Integer> scores){
        this.gradeLevels = gradeLevels;
        Integer max = Integer.MIN_VALUE;
        Integer min = Integer.MAX_VALUE;
        int sum = 0;
        for(Integer amount : scores) {
            if (amount < min) {
                min = amount;
            }
            if (amount > max) {
                max = amount;
            }
            sum += amount;
        }
        this.lowScore = min;
        this.highScore = max;
        this.spread = highScore - lowScore == 0 ? 1 : highScore - lowScore;
        if( scores.isEmpty()){
            this.mean = 0;
        } else {
            this.mean = sum / scores.size();
        }
    }

    public Grade assignGrade(Chamber chamber, int rawScore){
        int percent = (rawScore - lowScore) * 100 / spread;
        return new Grade(percent, chamber, gradeLevels);
    }

    public int getLowScore() {
        return lowScore;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getMean() {
        return mean;
    }
}
