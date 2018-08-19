package org.center4racialjustice.legup.domain;

public class ChamberConverter implements CodedEnumConverter<Chamber> {

    public static ChamberConverter INSTANCE = new ChamberConverter();

    @Override
    public Chamber fromCode(String code) {
        return Chamber.fromString(code);
    }

    @Override
    public String toCode(Chamber instance) {
        return instance.toString();
    }
}
