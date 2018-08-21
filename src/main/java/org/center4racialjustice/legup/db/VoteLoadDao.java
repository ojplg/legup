package org.center4racialjustice.legup.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.domain.VoteLoad;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class VoteLoadDao {

    private static final Logger log = LogManager.getLogger(VoteDao.class);

    public static final String table = "VOTE_LOADS";

    public static final List<TypedColumn<VoteLoad>> dataColumns =
            Arrays.asList(
                    new LongColumn<>("ID", "a", VoteLoad::getId, VoteLoad::setId),
                    new LocalDateTimeColumn<>("LOAD_TIME", "a", VoteLoad::getLoadTime, VoteLoad::setLoadTime),
                    new StringColumn<>("URL", "a", VoteLoad::getUrl, VoteLoad::setUrl),
                    new StringColumn<>("CHECKSUM", "a", VoteLoad::getCheckSum, VoteLoad::setCheckSum)
            );

    public final JoinColumn<Vote,Bill> billColumn =
            new JoinColumn<>("BILL_ID", "b", "bills", Vote::getBill, Vote::setBill,
                    BillDao.supplier, BillDao.typedColumnList );

    private final List<JoinColumn<Vote, ?>> joinColumns =
            Arrays.asList( billColumn );

    private final Connection connection;

    public static final Supplier<VoteLoad> supplier = () -> new VoteLoad();

    public VoteLoadDao(Connection connection) {
        this.connection = connection;
    }


}
