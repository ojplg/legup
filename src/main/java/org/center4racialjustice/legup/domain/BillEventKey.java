package org.center4racialjustice.legup.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class BillEventKey {

    private static final DateTimeFormatter KEY_FORMAT = DateTimeFormatter.ISO_DATE;

    private final String keyValue;

    public BillEventKey(LocalDate localDate, Chamber chamber, String rawContents){
        keyValue = localDate.format(KEY_FORMAT) + "|" + chamber.lowerCaseName() + "|" + rawContents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillEventKey that = (BillEventKey) o;
        return keyValue.equals(that.keyValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyValue);
    }
}
