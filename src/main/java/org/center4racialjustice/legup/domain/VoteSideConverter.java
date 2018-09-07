package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.db.hrorm.Converter;

public class VoteSideConverter implements Converter<String, VoteSide> {

    public static VoteSideConverter INSTANCE = new VoteSideConverter();

    @Override
    public String to(VoteSide voteSide) {
        return voteSide.getCode();
    }

    @Override
    public VoteSide from(String s) {
        return VoteSide.fromCode(s);
    }
}
