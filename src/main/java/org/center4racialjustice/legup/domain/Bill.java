package org.center4racialjustice.legup.domain;

import java.util.Objects;

public class Bill {

    private final int number;
    private final Chamber chamber;

    public Bill(int number, Chamber chamber){
        this.number = number;
        this.chamber = chamber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return number == bill.number &&
                Objects.equals(chamber, bill.chamber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, chamber);
    }
}
