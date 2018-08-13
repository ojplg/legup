package org.center4racialjustice.legup.domain;

import java.util.Objects;

public class Legislator {

    private final String name;
    private final Assembly assembly;

    public Legislator(String name, Assembly assembly) {
        this.name = name;
        this.assembly = assembly;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Legislator that = (Legislator) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(assembly, that.assembly);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, assembly);
    }
}
