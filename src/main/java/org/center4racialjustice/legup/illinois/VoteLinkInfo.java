package org.center4racialjustice.legup.illinois;

import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Chamber;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
@Data
public class VoteLinkInfo {

    private final String code;
    private final String voteDescription;
    private final LocalDate voteDate;
    private final Chamber chamber;
    private final boolean committee;
    private final String pdfUrl;

    private static final Logger log = LogManager.getLogger(VoteLinkInfo.class);

    private static final Pattern LinkTextPattern = Pattern.compile(
            "(\\w+) - ([\\w\\s\\&-]+?) - (?:\\w+, )?([A-Z][a-z]+ \\d+, \\d+)"
    );

    public static final DateTimeFormatter LongDateFormatter =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US);

    public static final DateTimeFormatter ShortDateFormatter =
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US);

    public static VoteLinkInfo create(String linkText, Chamber chamber, boolean committee, String pdfUrl) {
        Matcher matcher = LinkTextPattern.matcher(linkText);
        if (matcher.matches()) {
            String codeString = matcher.group(1);
            String descriptionString = matcher.group(2);
            String dateString = matcher.group(3);

            LocalDate voteDate;
            try {
                voteDate = LocalDate.parse(dateString, LongDateFormatter);
            } catch (DateTimeParseException ignore) {
                voteDate = LocalDate.parse(dateString, ShortDateFormatter);
            }

            VoteLinkInfo voteLinkInfo = VoteLinkInfo.builder()
                    .voteDate(voteDate)
                    .chamber(chamber)
                    .committee(committee)
                    .code(codeString)
                    .voteDescription(descriptionString)
                    .pdfUrl(pdfUrl)
                    .build();
            return voteLinkInfo;
        }

        log.warn("no match for " + linkText);
        return null;
    }

}
