package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportFactor;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.domain.VoteSideConverter;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ReportFactorDao {
    public static String table = "REPORT_FACTORS";

    public static List<TypedColumn<ReportFactor>> dataColumns =
            Arrays.asList(
                    new LongColumn<>("ID", "", ReportFactor::getId, ReportFactor::setId),
                    new CodedEnumColumn<>("VOTE_SIDE", "", ReportFactor::getVoteSide, ReportFactor::setVoteSide, VoteSideConverter.INSTANCE)
            );

    private static final JoinColumn<ReportFactor,Bill> billColumn =
            new JoinColumn<>("BILL_ID", "b", "bills", ReportFactor::getBill, ReportFactor::setBill,
                    BillDao.supplier, BillDao.typedColumnList );

    public static List<JoinColumn<ReportFactor, ?>> joinColumns =
            Arrays.asList( billColumn );

    public static Supplier<ReportFactor> supplier = () -> new ReportFactor();

    private final Connection connection;

    public ReportFactorDao(Connection connection) {
        this.connection = connection;
    }

    public List<ReportFactor> readByReportCardId(long reportCardId){
        StringBuilder sqlBldr = new StringBuilder();
        sqlBldr.append(DaoHelper.selectString(table, dataColumns));
        sqlBldr.append(" where report_card_id = '");
        sqlBldr.append(reportCardId);
        String sql = sqlBldr.toString();

        List<ReportFactor> factors = DaoHelper.read(connection, sql, dataColumns, supplier);
        return factors;
    }
}
