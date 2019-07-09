package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.VoteSide;

import java.util.ArrayList;
import java.util.List;


public class BillVotes {

    private final String content;
    private final String url;
    private Chamber billChamber;
    private Chamber votingChamber;
    private int billNumber;
    private long session;
    private int expectedNays;
    private int expectedYeas;
    private int expectedPresent;
    private int expectedNotVoting;
    private List<Name> nays = new ArrayList<>();
    private List<Name> yeas = new ArrayList<>();
    private List<Name> presents = new ArrayList<>();
    private List<Name> notVotings = new ArrayList<>();
    private List<Name> excuseds = new ArrayList<>();
    private List<Name> absents = new ArrayList<>();

    public BillVotes(){
        this.content = "";
        this.url = "";
    }

    public BillVotes(String url, String content){
        this.url = url;
        this.content = content;
    }

    public String getUrl() { return url; }

    public Long getChecksum(){
        return (long) content.hashCode();
    }

    public void addVoteRecords(List<VoteRecord> voteRecords){
        for(VoteRecord vr : voteRecords){
            addVoteRecord(vr);
        }
    }

    public void addVoteRecord(VoteRecord voteRecord){
        switch (voteRecord.getVote().getCode()){
            case VoteSide.PresentCode :
                presents.add(voteRecord.getName());
                break;
            case VoteSide.NayCode :
                nays.add(voteRecord.getName());
                break;
            case VoteSide.YeaCode :
                yeas.add(voteRecord.getName());
                break;
            case VoteSide.NotVotingCode :
                notVotings.add(voteRecord.getName());
                break;
            case VoteSide.AbsentCode :
                absents.add(voteRecord.getName());
                break;
            case VoteSide.ExcusedCode :
                excuseds.add(voteRecord.getName());
                break;
            default :
                throw new RuntimeException("Unrecognized vote type in " + voteRecord);
        }
    }

    public List<Name> getNays(){
        return nays;
    }

    public List<Name> getYeas(){
        return yeas;

    }
    public List<Name> getPresents(){
        return presents;
    }

    public List<Name> getNotVotings(){
        return notVotings;
    }

    public int totalVotes(){
        return nays.size() + yeas.size() + presents.size() + notVotings.size();
    }

    public void checkVoteCounts(){
        if (nays.size() != expectedNays){
            throw new RuntimeException("Bad Nays count. Expected " + expectedNays + " calculated " + nays.size());
        }
        if (yeas.size() != expectedYeas){
            throw new RuntimeException("Bad Yeas count. Expected " + expectedYeas + " calculated " + yeas.size());
        }
        if (presents.size() != expectedPresent){
            throw new RuntimeException("Bad Present count. Expected " + expectedPresent + " calculated " + presents.size());
        }
    }

    public Chamber getBillChamber() {
        return billChamber;
    }

    public void setBillChamber(Chamber billChamber) {
        this.billChamber = billChamber;
    }

    public Chamber getVotingChamber() {
        return votingChamber;
    }

    public void setVotingChamber(Chamber votingChamber) {
        this.votingChamber = votingChamber;
    }

    public int getBillNumber() {
        return billNumber;
    }

    public String getContent(){
        return content;
    }

    public void setBillNumber(int billNumber) {
        this.billNumber = billNumber;
    }

    public int getExpectedNays() {
        return expectedNays;
    }

    public void setExpectedNays(int expectedNays) {
        this.expectedNays = expectedNays;
    }

    public int getExpectedYeas() {
        return expectedYeas;
    }

    public void setExpectedYeas(int expectedYeas) {
        this.expectedYeas = expectedYeas;
    }

    public int getExpectedPresent() {
        return expectedPresent;
    }

    public void setExpectedPresent(int expectedPresent) {
        this.expectedPresent = expectedPresent;
    }

    public int getExpectedNotVoting() {
        return expectedNotVoting;
    }

    public void setExpectedNotVoting(int expectedNotVoting) {
        this.expectedNotVoting = expectedNotVoting;
    }

    public long getSession() {
        return session;
    }

    public void setSession(long session) {
        this.session = session;
    }
}
