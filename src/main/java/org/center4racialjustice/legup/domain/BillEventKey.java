package org.center4racialjustice.legup.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class BillEventKey {

    private static final DateTimeFormatter KEY_FORMAT = DateTimeFormatter.ISO_DATE;

    private final String keyValue;

    public BillEventKey(LocalDate localDate, Chamber chamber, String rawContents){
        String dateString;
        if ( localDate != null ){
            dateString = localDate.format(KEY_FORMAT);
        } else {
            dateString = "UNKNOWN DATE";
        }
        String chamberString;
        if ( chamber != null){
            chamberString = chamber.lowerCaseName();
        } else {
            chamberString = "UNKNOWN CHAMBER";
        }

        keyValue = dateString + "|" + chamberString + "|" + rawContents;
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
