package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.util.Tuple;

import java.util.ArrayList;
import java.util.List;

@Data
public class SponsorNames {
    private Tuple<String, String> houseChiefSponsor = new Tuple<>("","");
    private Tuple<String, String> senateChiefSponsor = new Tuple<>("","");
    private List<Tuple<String, String>> houseSponsors = new ArrayList<>();
    private List<Tuple<String, String>> senateSponsors = new ArrayList<>();
}
