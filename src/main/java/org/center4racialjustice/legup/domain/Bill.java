package org.center4racialjustice.legup.domain;

import java.util.Objects;

public class Bill {

    private final int number;
    private final Assembly assembly;

    public Bill(int number, Assembly assembly){
        this.number = number;
        this.assembly = assembly;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return number == bill.number &&
                Objects.equals(assembly, bill.assembly);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, assembly);
    }
}
