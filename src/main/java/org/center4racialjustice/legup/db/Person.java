package org.center4racialjustice.legup.db;

import lombok.Data;

@Data
public class Person {
    private Long id;
    private String prefix;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
}
