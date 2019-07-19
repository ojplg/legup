package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.domain.VoteType;

@Data
public class BillWebData {

    private final String url;
    private final String pdfContents;

    public long getChecksum(){
        return (long) pdfContents.hashCode();
    }
}
