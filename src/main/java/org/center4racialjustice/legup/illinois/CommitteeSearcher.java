package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Committee;
import org.center4racialjustice.legup.domain.CommitteeMember;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommitteeSearcher {

    private static final List<String> COMMITTEES_TO_SKIP = Arrays.asList(
            "Committee of the Whole"
    );

    private static final String BASE_URL = "http://www.ilga.gov/house/committees/";

    private static final Logger log = LogManager.getLogger(CommitteeSearcher.class);

    private final Chamber chamber;
    private final NameParser nameParser;
    private final List<Legislator> legislators;

    public CommitteeSearcher(Chamber chamber, NameParser nameParser, List<Legislator> legislators) {
        this.chamber = chamber;
        this.nameParser = nameParser;
        this.legislators = legislators;
    }

    private String formUrl(){
        return "http://www.ilga.gov/" + chamber.lowerCaseName() +  "/committees/default.asp";
    }

    public List<Committee> search(){
        String listUrl = formUrl();
        log.info("Searching for committees at " + listUrl);
        CommitteeListHtmlParser committeeListHtmlParser = CommitteeListHtmlParser.load(listUrl);
        List<Triple<String, String, String>> committeeData = committeeListHtmlParser.parseCommitteeLinks();
        List<Committee> committeeList = new ArrayList<>();
        for(Triple<String, String, String> committeeTriplet : committeeData){
            log.info("Working on committee " + committeeTriplet);
            // FIXME: What a hack
            if( COMMITTEES_TO_SKIP.contains(committeeTriplet.getFirst())){
                log.info("Skipping " + committeeTriplet);
                continue;
            }
            String memberUrl = committeeTriplet.getThird();
            CommitteeMemberParser committeeMemberParser = CommitteeMemberParser.load(BASE_URL + memberUrl, nameParser);
            List<Triple<String, Name, String>> members = committeeMemberParser.parseMembers();
            List<CommitteeMember> memberList = new ArrayList<>();
            for(Triple<String, Name, String> memberData : members){
                log.info("Working on committee member " + memberData);
                Legislator legislator = fromNameAndMemberId(memberData.getSecond(), memberData.getThird());
                CommitteeMember member = new CommitteeMember();
                member.setTitle(memberData.getFirst());
                member.setLegislator(legislator);
                memberList.add(member);
            }
            String committeeId = CommitteeListHtmlParser.getCommitteeId(committeeTriplet);
            Committee committee = new Committee();
            committee.setChamber(chamber);
            committee.setMembers(memberList);
            committee.setCommitteeId(committeeId);
            committee.setName(committeeTriplet.getFirst());
            committee.setCode(committeeTriplet.getSecond());
            committeeList.add(committee);
        }
        return committeeList;
    }

    private Legislator fromNameAndMemberId(Name name, String memberId){
        Legislator legislator =  Lists.findfirst(legislators,
                l -> memberId.equals(l.getMemberId()));
        return legislator;
    }

}
