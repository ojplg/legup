package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportFactor;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ReportCardDao {
    public static String table = "REPORT_CARDS";

    public static List<TypedColumn<ReportCard>> dataColumns =
            Arrays.asList(
                    new LongColumn<>("ID", "", ReportCard::getId, ReportCard::setId),
                    new StringColumn<>("NAME", "", ReportCard::getName, ReportCard::setName),
                    new LongColumn<>("SESSION_NUMBER", "", ReportCard::getSessionNumber, ReportCard::setSessionNumber)
            );

    public static Supplier<ReportCard> supplier = () -> new ReportCard();

    private final Connection connection;
    private final ReportFactorDao reportFactorDao;

    public ReportCardDao(Connection connection) {
        this.connection = connection;
        this.reportFactorDao = new ReportFactorDao(connection);
    }

    public long save(ReportCard reportCard){
        // simply delete all old factors for now. it's simple
        if( reportCard.getId() != null) {
            reportFactorDao.deleteByReportCardId(reportCard.getId());
        }
        long reportCardId = DaoHelper.save(connection, table, dataColumns, reportCard);
        for(ReportFactor factor : reportCard.getReportFactors()){
            factor.setReportCardId(reportCardId);
            reportFactorDao.save(factor);
        }
        return reportCardId;
    }

    public List<ReportCard> readAll(){
        return readCards( Collections.emptyList() );
    }

    public ReportCard read(long id){
        List<ReportCard> reportCards = readCards( Collections.singletonList(id));
        return DaoHelper.fromSingletonList(reportCards, "Reading report card for " + id);
    }

    private List<ReportCard> readCards(List<Long> ids){
        List<ReportCard> reportCards = DaoHelper.read(connection, table, dataColumns, ids, supplier);
        for(ReportCard reportCard : reportCards){
            List<ReportFactor> factors = reportFactorDao.readByReportCardId(reportCard.getId());
            reportCard.setReportFactors(factors);
        }
        return reportCards;
    }


}
