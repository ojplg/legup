package org.center4racialjustice.legup.domain;

public class Grader {

    private final int lowScore;
    private final int spread;

    public Grader(int lowScore, int highScore){
        this.lowScore = lowScore;
        this.spread = highScore - lowScore;
    }

    public Grade assignGrade(int rawScore){
        int percent = (rawScore - lowScore) * 100 / spread;
        return new Grade(percent);
    }

}
