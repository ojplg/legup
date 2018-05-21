package org.center4racialjustice.legup.illinois;

import java.util.ArrayList;
import java.util.List;

public class BillVotes {
    String content;
    int billNumber;
    int expectedNays;
    int expectedYeas;
    int expectedPresent;
    int expectedNotVoting;
    List<Name> nays = new ArrayList<>();
    List<Name> yeas = new ArrayList<>();
    List<Name> presents = new ArrayList<>();
    List<Name> notVotings = new ArrayList<>();
}
