package org.center4racialjustice.legup.db.hrorm;

public class BooleanConverter implements Converter<String, Boolean> {

    public static final BooleanConverter INSTANCE = new BooleanConverter();

    @Override
    public String to(Boolean aBoolean) {
        if ( aBoolean == null ) {
            return null;
        }
        return aBoolean ? "T" : "F";
    }

    @Override
    public Boolean from(String s) {
        if ( s == null ){
            return null;
        }
        switch (s) {
            case "T" : return Boolean.TRUE;
            case "F" : return Boolean.FALSE;
            default : throw new RuntimeException("Unsupported string: " + s);
        }
    }
}
