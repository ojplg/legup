import java.util.ArrayList;
import java.util.List;

public class BillVotes {
    String content;
    int billNumber;
    int expectedNays;
    int expectedYeas;
    int expectedPresent;
    int expectedNotVoting;
    List<String> nays = new ArrayList<>();
    List<String> yeas = new ArrayList<>();
    List<String> presents = new ArrayList<>();
    List<String> notVotings = new ArrayList<>();
}
