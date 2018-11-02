package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String email;
    private String password;
    private String salt;
    private Organization organization;
}
