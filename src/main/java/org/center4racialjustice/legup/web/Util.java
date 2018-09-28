package org.center4racialjustice.legup.web;

public class Util {

    public static String classNameToLowercaseWithUnderlines(Class class_){
        String className = class_.getSimpleName();
        return convertTitleCaseToLowercaseWithUnderlines(className);
    }

    public static String convertTitleCaseToLowercaseWithUnderlines(String name) {
        StringBuilder buf = new StringBuilder();
        for (int idx = 0; idx < name.length(); idx++) {
            char c = name.charAt(idx);
            if (idx == 0) {
                buf.append(Character.toLowerCase(c));
            } else if(Character.isUpperCase(c)) {
                buf.append('_');
                buf.append(Character.toLowerCase(c));
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }

}
