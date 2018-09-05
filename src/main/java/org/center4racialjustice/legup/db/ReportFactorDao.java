package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.ReportFactor;
import org.center4racialjustice.legup.domain.VoteSideConverter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ReportFactorDao {
    public static String table = "REPORT_FACTORS";

    public static List<TypedColumn<ReportFactor>> dataColumns =
            Arrays.asList(
                    new LongColumn<>("ID", "a", ReportFactor::getId, ReportFactor::setId),
                    new LongColumn<>("REPORT_CARD_ID", "a", ReportFactor::getReportCardId, ReportFactor::setReportCardId),
                    new CodedEnumColumn<>("VOTE_SIDE", "a", ReportFactor::getVoteSide, ReportFactor::setVoteSide, VoteSideConverter.INSTANCE)
            );

    private static final JoinColumn<ReportFactor,Bill> billColumn =
            new JoinColumn<>("BILL_ID", "b", BillDao.table, ReportFactor::getBill, ReportFactor::setBill,
                    BillDao.supplier, BillDao.typedColumnList );

    public static List<JoinColumn<ReportFactor, ?>> joinColumns =
            Arrays.asList( billColumn );

    public static Supplier<ReportFactor> supplier = () -> new ReportFactor();

    private final Connection connection;
    private final org.center4racialjustice.legup.db.hrorm.Dao<ReportFactor> innerDao;

    public ReportFactorDao(Connection connection) {
        this.connection = connection;
        this.innerDao = DaoBuilders.REPORT_FACTORS.buildDao(connection);
    }

    public List<ReportFactor> readByReportCardId(long reportCardId){
        ReportFactor reportFactor = new ReportFactor();
        reportFactor.setReportCardId(reportCardId);
        return innerDao.selectManyByColumns(reportFactor, Arrays.asList("REPORT_CARD_ID"));
    }

    public void deleteByReportCardId(long reportCardId){
        try {
            Statement statement = connection.createStatement();
            statement.execute("delete from report_factors where report_card_id = " + reportCardId);
            statement.close();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    public Long save(ReportFactor reportFactor){
        return innerDao.insert(reportFactor);
    }
}
