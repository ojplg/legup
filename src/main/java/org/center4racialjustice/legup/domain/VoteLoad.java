package org.center4racialjustice.legup.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VoteLoad implements Identifiable {

    private Long id;
    private LocalDateTime loadTime;
    private Bill bill;
    private String url;
    private String checkSum;

}
