package org.center4racialjustice.legup.domain;

public class VoteSideConverter implements CodedEnumConverter<VoteSide> {

    public static VoteSideConverter INSTANCE = new VoteSideConverter();

    @Override
    public VoteSide fromCode(String code) {
        return VoteSide.fromCode(code);
    }

    @Override
    public String toCode(VoteSide instance) {
        return instance.getCode();
    }
}
