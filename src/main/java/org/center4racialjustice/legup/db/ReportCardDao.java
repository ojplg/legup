package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.ReportCard;

import java.sql.Connection;
import java.util.ArrayList;
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

    public ReportCardDao(Connection connection) {
        this.connection = connection;
    }

    public long save(ReportCard reportCard){
        return DaoHelper.save(connection, table, dataColumns, reportCard);
    }

    public List<ReportCard> readAll(){
        return DaoHelper.read(connection, table, dataColumns, Collections.emptyList(), supplier);
    }

    public ReportCard read(long id){
        List<ReportCard> reportCards = DaoHelper.read(connection, table, dataColumns, Collections.singletonList(id), supplier);
        return DaoHelper.fromSingletonList(reportCards, "Reading report card for " + id);
    }
}
