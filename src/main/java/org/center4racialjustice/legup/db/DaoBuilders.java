package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.db.hrorm.DaoBuilder;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportFactor;
import org.center4racialjustice.legup.domain.VoteSideConverter;

public class DaoBuilders {

    public static final DaoBuilder<Bill> BILLS = billDaoBuilder();
    public static final DaoBuilder<Legislator> LEGISLATORS = legislatorDaoBuilder();
    public static final DaoBuilder<ReportFactor> REPORT_FACTORS = reportFactorDaoBuilder();
    public static final DaoBuilder<ReportCard> REPORT_CARDS = reportCardDaoBuilder();

    private static DaoBuilder<Bill> billDaoBuilder(){
        return new DaoBuilder<>("BILLS", Bill::new)
                .withPrimaryKey("ID", Bill::getId, Bill::setId)
                .withConvertingStringColumn("CHAMBER", Bill::getChamber, Bill::setChamber, Chamber.Converter)
                .withIntegerColumn("BILL_NUMBER", Bill::getNumber, Bill::setNumber)
                .withIntegerColumn("SESSION_NUMBER", Bill::getSession, Bill::setSession)
                .withStringColumn("SHORT_DESCRIPTION", Bill::getShortDescription, Bill::setShortDescription);
    }

    private static DaoBuilder<Legislator> legislatorDaoBuilder(){
        return new DaoBuilder<>("LEGISLATORS", Legislator::new)
                .withPrimaryKey("ID", Legislator::getId, Legislator::setId)
                .withStringColumn("FIRST_NAME", Legislator::getFirstName, Legislator::setFirstName)
                .withStringColumn("MIDDLE_NAME_OR_INITIAL", Legislator::getMiddleInitialOrName, Legislator::setMiddleInitialOrName)
                .withStringColumn("LAST_NAME", Legislator::getLastName, Legislator::setLastName)
                .withStringColumn("SUFFIX", Legislator::getSuffix, Legislator::setSuffix)
                .withConvertingStringColumn("CHAMBER", Legislator::getChamber, Legislator::setChamber, Chamber.Converter)
                .withIntegerColumn("DISTRICT", Legislator::getDistrict, Legislator::setDistrict)
                .withStringColumn("PARTY", Legislator::getParty, Legislator::setParty)
                .withIntegerColumn("SESSION_NUMBER", Legislator::getSessionNumber, Legislator::setSessionNumber)
                .withStringColumn("MEMBER_ID", Legislator::getMemberId, Legislator::setMemberId);
    }

    private static DaoBuilder<ReportFactor> reportFactorDaoBuilder(){
        return new DaoBuilder<>("REPORT_FACTORS", ReportFactor::new)
                .withPrimaryKey("ID", ReportFactor::getId, ReportFactor::setId)
                .withIntegerColumn("REPORT_CARD_ID", ReportFactor::getReportCardId, ReportFactor::setReportCardId)
                .withConvertingStringColumn("VOTE_SIDE", ReportFactor::getVoteSide, ReportFactor::setVoteSide, VoteSideConverter.INSTANCE)
                .withJoinColumn("BILL_ID", ReportFactor::getBill, ReportFactor::setBill, BILLS);
    }

    private static DaoBuilder<ReportCard> reportCardDaoBuilder(){
        return new DaoBuilder<>("REPORT_CARDS", ReportCard::new)
                .withPrimaryKey("ID", ReportCard::getId, ReportCard::setId)
                .withStringColumn("NAME", ReportCard::getName, ReportCard::setName)
                .withIntegerColumn("SESSION_NUMBER", ReportCard::getSessionNumber, ReportCard::setSessionNumber);
    }
}
