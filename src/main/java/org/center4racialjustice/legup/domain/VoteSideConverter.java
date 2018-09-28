package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.db.hrorm.Converter;

public class VoteSideConverter implements Converter<VoteSide, String> {

    public static VoteSideConverter INSTANCE = new VoteSideConverter();

    @Override
    public String from(VoteSide voteSide) {
        return voteSide.getCode();
    }

    @Override
    public VoteSide to(String s) {
        return VoteSide.fromCode(s);
    }
}
