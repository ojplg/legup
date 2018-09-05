package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.db.hrorm.Converter;

public class VoteSideConverter implements CodedEnumConverter<VoteSide>, Converter<String, VoteSide> {

    public static VoteSideConverter INSTANCE = new VoteSideConverter();

    @Override
    public VoteSide fromCode(String code) {
        return VoteSide.fromCode(code);
    }

    @Override
    public String toCode(VoteSide instance) {
        return instance.getCode();
    }

    @Override
    public String to(VoteSide voteSide) {
        return voteSide.getCode();
    }

    @Override
    public VoteSide from(String s) {
        return VoteSide.fromCode(s);
    }
}
