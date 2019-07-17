package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.util.Dates;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class BillActionLoad {

    public static final DateTimeFormatter Formatter =
            DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss");

    private Long id;
    private Instant loadInstant;
    private Bill bill;
    private String url;
    private long checkSum;

    public boolean matches(String url, long checkSum){
        return this.url.equals(url) && this.checkSum == checkSum;
    }

    public static BillActionLoad create(Bill bill, String url, long checksum){
        BillActionLoad load = new BillActionLoad();
        load.loadInstant = Instant.now();
        load.bill = bill;
        load.url = url;
        load.checkSum = checksum;
        return load;
    }

    public static String formKey(Bill bill, String url, boolean isVoteLoad){
        String ext;
        if ( isVoteLoad ){
            int start = url.lastIndexOf('/') + 1;
            int end = url.lastIndexOf('.');
            ext = url.substring(start, end);
        } else {
            ext = "main";
        }
        return bill.getKey() + "." + ext;
    }

    public LocalDateTime getLoadTime(){
        return Dates.localDateTimeOf(loadInstant);
    }

    public String getFormattedLoadTime() {
        return Formatter.format(getLoadTime());
    }

    public boolean isVoteLoad(){
        // TODO: Should be added to the model and written to the table
        return url.contains("http://www.ilga.gov/legislation/votehistory");
    }

    public boolean isBillLoad(){
        // TODO: See above
        return url.contains("http://www.ilga.gov/legislation/BillStatus.asp");
    }

    public String getKey(){
        return formKey(this.bill, this.url, isVoteLoad());
    }
}
