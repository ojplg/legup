package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.util.Tuple;

import java.util.ArrayList;
import java.util.List;

@Data
public class SponsorNames {
    private Tuple<String, String> houseChiefSponsor;
    private Tuple<String, String> senateChiefSponsor;
    private List<Tuple<String, String>> houseSponsors = new ArrayList<>();
    private List<Tuple<String, String>> senateSponsors = new ArrayList<>();
}
