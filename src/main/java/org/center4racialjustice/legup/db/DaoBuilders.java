package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.db.hrorm.DaoBuilder;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportCardLegislator;
import org.center4racialjustice.legup.domain.ReportFactor;
import org.center4racialjustice.legup.domain.VoteSideConverter;

public class DaoBuilders {

    public static final DaoBuilder<Bill> BILLS = billDaoBuilder();
    public static final DaoBuilder<Legislator> LEGISLATORS = legislatorDaoBuilder();
    public static final DaoBuilder<ReportFactor> REPORT_FACTORS = reportFactorDaoBuilder();
    public static final DaoBuilder<ReportCardLegislator> REPORT_CARD_LEGISLATORS = reportCardLegislatorDaoBuilder();
    public static final DaoBuilder<ReportCard> REPORT_CARDS = reportCardDaoBuilder();
    public static final DaoBuilder<BillActionLoad> BILL_ACTION_LOADS = billActionLoadDaoBuilder();
    public static final DaoBuilder<BillAction> BILL_ACTIONS = billActionDaoBuilder();

    private static DaoBuilder<Bill> billDaoBuilder(){
        return new DaoBuilder<>("BILLS", Bill::new)
                .withConvertingStringColumn("CHAMBER", Bill::getChamber, Bill::setChamber, Chamber.Converter)
                .withPrimaryKey("ID", "bill_seq", Bill::getId, Bill::setId)
                .withIntegerColumn("BILL_NUMBER", Bill::getNumber, Bill::setNumber)
                .withIntegerColumn("SESSION_NUMBER", Bill::getSession, Bill::setSession)
                .withStringColumn("SHORT_DESCRIPTION", Bill::getShortDescription, Bill::setShortDescription);
    }

    private static DaoBuilder<Legislator> legislatorDaoBuilder(){
        return new DaoBuilder<>("LEGISLATORS", Legislator::new)
                .withStringColumn("FIRST_NAME", Legislator::getFirstName, Legislator::setFirstName)
                .withStringColumn("MIDDLE_NAME_OR_INITIAL", Legislator::getMiddleInitialOrName, Legislator::setMiddleInitialOrName)
                .withStringColumn("LAST_NAME", Legislator::getLastName, Legislator::setLastName)
                .withStringColumn("SUFFIX", Legislator::getSuffix, Legislator::setSuffix)
                .withPrimaryKey("ID", "legislator_seq", Legislator::getId, Legislator::setId)
                .withConvertingStringColumn("CHAMBER", Legislator::getChamber, Legislator::setChamber, Chamber.Converter)
                .withIntegerColumn("DISTRICT", Legislator::getDistrict, Legislator::setDistrict)
                .withStringColumn("PARTY", Legislator::getParty, Legislator::setParty)
                .withIntegerColumn("SESSION_NUMBER", Legislator::getSessionNumber, Legislator::setSessionNumber)
                .withBooleanColumn("COMPLETE_TERM", Legislator::getCompleteTerm, Legislator::setCompleteTerm)
                .withStringColumn("MEMBER_ID", Legislator::getMemberId, Legislator::setMemberId);
    }

    private static DaoBuilder<ReportFactor> reportFactorDaoBuilder(){
        return new DaoBuilder<>("REPORT_FACTORS", ReportFactor::new)
                .withPrimaryKey("ID", "report_factor_seq", ReportFactor::getId, ReportFactor::setId)
                .withIntegerColumn("REPORT_CARD_ID", ReportFactor::getReportCardId, ReportFactor::setReportCardId)
                .withConvertingStringColumn("VOTE_SIDE", ReportFactor::getVoteSide, ReportFactor::setVoteSide, VoteSideConverter.INSTANCE)
                .withJoinColumn("BILL_ID", ReportFactor::getBill, ReportFactor::setBill, BILLS);
    }

    private static DaoBuilder<ReportCardLegislator> reportCardLegislatorDaoBuilder(){
        return new DaoBuilder<>("REPORT_CARD_LEGISLATORS", ReportCardLegislator::new)
                .withPrimaryKey("ID", "report_card_legislator_seq", ReportCardLegislator::getId, ReportCardLegislator::setId)
                .withIntegerColumn("REPORT_CARD_ID", ReportCardLegislator::getReportCardId, ReportCardLegislator::setId)
                .withJoinColumn("LEGISLATOR_ID", ReportCardLegislator::getLegislator, ReportCardLegislator::setLegislator, LEGISLATORS);
    }

    private static DaoBuilder<ReportCard> reportCardDaoBuilder(){
        return new DaoBuilder<>("REPORT_CARDS", ReportCard::new)
                .withPrimaryKey("ID", "report_card_seq", ReportCard::getId, ReportCard::setId)
                .withStringColumn("NAME", ReportCard::getName, ReportCard::setName)
                .withIntegerColumn("SESSION_NUMBER", ReportCard::getSessionNumber, ReportCard::setSessionNumber)
                .withChildren("REPORT_CARD_ID", ReportFactor::setReportCardId,
                        ReportCard::getReportFactors, ReportCard::setReportFactors, REPORT_FACTORS)
                .withChildren("REPORT_CARD_ID", ReportCardLegislator::setReportCardId,
                        ReportCard::getReportCardLegislators, ReportCard::setReportCardLegislators, REPORT_CARD_LEGISLATORS);
    }

    private static DaoBuilder<BillActionLoad> billActionLoadDaoBuilder(){
        return new DaoBuilder<>("BILL_ACTION_LOADS", BillActionLoad::new)
                .withPrimaryKey("ID", "bill_action_load_seq", BillActionLoad::getId, BillActionLoad::setId)
                .withLocalDateTimeColumn("LOAD_TIME", BillActionLoad::getLoadTime, BillActionLoad::setLoadTime)
                .withStringColumn("URL", BillActionLoad::getUrl, BillActionLoad::setUrl)
                .withIntegerColumn("CHECK_SUM", BillActionLoad::getCheckSum, BillActionLoad::setCheckSum)
                .withJoinColumn("BILL_ID", BillActionLoad::getBill, BillActionLoad::setBill, BILLS);
    }

    private static DaoBuilder<BillAction> billActionDaoBuilder(){
        return new DaoBuilder<>("BILL_ACTIONS", BillAction::new)
                .withPrimaryKey("ID", "bill_action_seq", BillAction::getId, BillAction::setId)
                .withConvertingStringColumn("BILL_ACTION_TYPE", BillAction::getBillActionType, BillAction::setBillActionType, BillActionType.CONVERTER)
                .withStringColumn("BILL_ACTION_DETAIL", BillAction::getBillActionDetail, BillAction::setBillActionDetail)
                .withJoinColumn("BILL_ID", BillAction::getBill, BillAction::setBill, BILLS)
                .withJoinColumn("LEGISLATOR_ID", BillAction::getLegislator, BillAction::setLegislator, LEGISLATORS)
                .withJoinColumn("BILL_ACTION_LOAD_ID", BillAction::getBillActionLoad, BillAction::setBillActionLoad, BILL_ACTION_LOADS);

    }
}
