package org.center4racialjustice.legup.db;

import lombok.Data;

@Data
public class Legislator implements Identifiable {
    private Long id;
    private Long district;
    private String party;
    private String assembly;
    private Long year;
    private Person person;
}
