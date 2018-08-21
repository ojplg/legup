package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.ReportCard;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ReportCardDao {
    public static String table = "REPORT_CARDS";

    public static List<TypedColumn<ReportCard>> typedColumnList =
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

    public List<ReportCard> readAll(){
        List<ReportCard> cards = new ArrayList<>();

        return cards;
    }
}
