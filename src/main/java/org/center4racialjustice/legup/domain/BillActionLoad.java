package org.center4racialjustice.legup.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BillActionLoad {

    private Long id;
    private LocalDateTime loadTime;
    private Bill bill;
    private String url;
    private long checkSum;

    public boolean matches(String url, long checkSum){
        return this.url.equals(url) && this.checkSum == checkSum;
    }

}
