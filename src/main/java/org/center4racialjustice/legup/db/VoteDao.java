package org.center4racialjustice.legup.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.domain.VoteLoad;
import org.center4racialjustice.legup.domain.VoteSideConverter;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class VoteDao {

    private static final Logger log = LogManager.getLogger(VoteDao.class);

    private final String table = "VOTES";

    private final List<TypedColumn<Vote>> dataColumns =
            Arrays.asList(
                    new LongColumn<>("ID", "a", Vote::getId, Vote::setId),
                    new CodedEnumColumn<>("VOTE_SIDE", "a", Vote::getVoteSide, Vote::setVoteSide, new VoteSideConverter())
            );

    private final JoinColumn<Vote,Bill> billColumn =
            new JoinColumn<>("BILL_ID", "b", "bills", Vote::getBill, Vote::setBill,
                    BillDao.supplier, BillDao.typedColumnList );

    private final JoinColumn<Vote,Legislator> legislatorColumn =
            new JoinColumn<>("LEGISLATOR_ID", "c", "legislators", Vote::getLegislator, Vote::setLegislator,
                    LegislatorDao.supplier, LegislatorDao.typedColumnList );

    private final JoinColumn<Vote,VoteLoad> voteLoadColumn =
            new JoinColumn<>("VOTE_LOAD_ID", "d", VoteLoadDao.table, Vote::getVoteLoad, Vote::setVoteLoad,
                    VoteLoadDao.supplier, VoteLoadDao.dataColumns );

    private final List<JoinColumn<Vote, ?>> joinColumns =
            Arrays.asList( billColumn, legislatorColumn );

    private final Connection connection;

    public final Supplier<Vote> supplier = () -> new Vote();

    public VoteDao(Connection connection) {
        this.connection = connection;
    }

    public long insert(Vote vote){
        return DaoHelper.doInsert(connection, table, dataColumns, joinColumns, vote);
    }

    public List<Vote> readByLegislator(Legislator legislator){
        StringBuilder buf = new StringBuilder();

        buf.append(DaoHelper.joinSelectSql(table, dataColumns, Collections.singletonList(billColumn)));

        buf.append(" and a.legislator_id = ");
        buf.append(legislator.getId());
        String sql = buf.toString();

        List<Vote> votes = DaoHelper.doSelect(connection, sql, supplier, dataColumns, Collections.singletonList(billColumn));

        votes.forEach(v -> v.setLegislator(legislator));

        return votes;

    }

    public List<Vote> readByBill(Bill bill){
        StringBuilder buf = new StringBuilder();

        buf.append(DaoHelper.joinSelectSql(table, dataColumns, Collections.singletonList(legislatorColumn)));

        buf.append(" and a.bill_id = ");
        buf.append(bill.getId());
        String sql = buf.toString();

        List<Vote> votes = DaoHelper.doSelect(connection, sql, supplier, dataColumns, Collections.singletonList(legislatorColumn));

        votes.forEach(v -> v.setBill(bill));

        return votes;
    }

    public Vote read(long id){

        StringBuilder buf = new StringBuilder();

        buf.append(DaoHelper.joinSelectSql(table, dataColumns, joinColumns));

        buf.append(" and a.id = ");
        buf.append(id);

        String sql = buf.toString();

        List<Vote> votes = DaoHelper.doSelect(connection, sql, supplier, dataColumns, joinColumns);

        return DaoHelper.fromSingletonList(votes, "Reading vote");
    }

}
