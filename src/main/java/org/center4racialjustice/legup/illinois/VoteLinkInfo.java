package org.center4racialjustice.legup.illinois;

import lombok.Builder;
import lombok.Data;
import org.center4racialjustice.legup.domain.Chamber;

import java.time.LocalDate;

@Data
@Builder
public class VoteLinkInfo {

    private final String code;
    private final String voteDescription;
    private final LocalDate voteDate;
    private final Chamber chamber;
    private final boolean committee;
    private final String pdfUrl;

}
