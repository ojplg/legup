package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Committee;
import org.center4racialjustice.legup.domain.CommitteeMember;
import org.center4racialjustice.legup.domain.GradeLevel;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.User;
import org.hrorm.AssociationDaoBuilder;
import org.hrorm.DaoBuilder;
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
    public static final DaoBuilder<GradeLevel> GRADE_LEVELS = gradeLevelDaoBuilder();
    public static final DaoBuilder<ReportCard> REPORT_CARDS = reportCardDaoBuilder();
    public static final DaoBuilder<BillActionLoad> BILL_ACTION_LOADS = billActionLoadDaoBuilder();
    public static final DaoBuilder<BillAction> BILL_ACTIONS = billActionDaoBuilder();
    public static final DaoBuilder<Organization> ORGANIZATIONS = organizationDaoBuilder();
    public static final DaoBuilder<User> USERS = userDaoBuilder();
    public static final AssociationDaoBuilder<User, Organization> USER_ORGANIZATION_ASSOCIATIONS = userOrganizationAssociationDaoBuilder();
    public static final DaoBuilder<CommitteeMember> COMMITTEE_MEMBERS = committeeMemberDaoBuilder();
    public static final DaoBuilder<Committee> COMMITTEE = committeeDaoBuilder();

    private static DaoBuilder<Bill> billDaoBuilder(){
        return new DaoBuilder<>("BILLS", Bill::new)
                .withConvertingStringColumn("CHAMBER", Bill::getChamber, Bill::setChamber, Chamber.Converter)
                .withPrimaryKey("ID", "bill_seq", Bill::getId, Bill::setId)
                .withLongColumn("BILL_NUMBER", Bill::getNumber, Bill::setNumber)
                .withLongColumn("SESSION_NUMBER", Bill::getSession, Bill::setSession)
                .withStringColumn("SHORT_DESCRIPTION", Bill::getShortDescription, Bill::setShortDescription)
                .withStringColumn("SUB_TYPE", Bill::getLegislationSubType, Bill::setLegislationSubType);
    }

    private static DaoBuilder<Legislator> legislatorDaoBuilder(){
        return new DaoBuilder<>("LEGISLATORS", Legislator::new)
                .withStringColumn("FIRST_NAME", Legislator::getFirstName, Legislator::setFirstName)
                .withStringColumn("MIDDLE_NAME_OR_INITIAL", Legislator::getMiddleInitialOrName, Legislator::setMiddleInitialOrName)
                .withStringColumn("LAST_NAME", Legislator::getLastName, Legislator::setLastName)
                .withStringColumn("SUFFIX", Legislator::getSuffix, Legislator::setSuffix)
                .withPrimaryKey("ID", "legislator_seq", Legislator::getId, Legislator::setId)
                .withConvertingStringColumn("CHAMBER", Legislator::getChamber, Legislator::setChamber, Chamber.Converter)
                .withLongColumn("DISTRICT", Legislator::getDistrict, Legislator::setDistrict)
                .withStringColumn("PARTY", Legislator::getParty, Legislator::setParty)
                .withLongColumn("SESSION_NUMBER", Legislator::getSessionNumber, Legislator::setSessionNumber)
                .withBooleanColumn("COMPLETE_TERM", Legislator::getCompleteTerm, Legislator::setCompleteTerm)
                .withStringColumn("MEMBER_ID", Legislator::getMemberId, Legislator::setMemberId);
    }

    private static DaoBuilder<ReportFactor> reportFactorDaoBuilder(){
        return new DaoBuilder<>("REPORT_FACTORS", ReportFactor::new)
                .withPrimaryKey("ID", "report_factor_seq", ReportFactor::getId, ReportFactor::setId)
                .withParentColumn("REPORT_CARD_ID", ReportFactor::getReportCard, ReportFactor::setReportCard)
                .withConvertingStringColumn("VOTE_SIDE", ReportFactor::getVoteSide, ReportFactor::setVoteSide, VoteSideConverter.INSTANCE)
                .withJoinColumn("BILL_ID", ReportFactor::getBill, ReportFactor::setBill, BILLS);
    }

    private static DaoBuilder<ReportCardLegislator> reportCardLegislatorDaoBuilder(){
        return new DaoBuilder<>("REPORT_CARD_LEGISLATORS", ReportCardLegislator::new)
                .withPrimaryKey("ID", "report_card_legislator_seq", ReportCardLegislator::getId, ReportCardLegislator::setId)
                .withParentColumn("REPORT_CARD_ID", ReportCardLegislator::getReportCard, ReportCardLegislator::setReportCard)
                .withJoinColumn("LEGISLATOR_ID", ReportCardLegislator::getLegislator, ReportCardLegislator::setLegislator, LEGISLATORS);
    }

    private static DaoBuilder<GradeLevel> gradeLevelDaoBuilder(){
        return new DaoBuilder<>("GRADE_LEVELS", GradeLevel::new)
                .withPrimaryKey("ID", "grade_level_seq", GradeLevel::getId, GradeLevel::setId)
                .withParentColumn("REPORT_CARD_ID")
                .withStringColumn("GRADE", GradeLevel::getGrade, GradeLevel::setGrade)
                .withLongColumn("PERCENTAGE", GradeLevel::getPercentage, GradeLevel::setPercentage)
                .withConvertingStringColumn("CHAMBER", GradeLevel::getChamber, GradeLevel::setChamber, Chamber.Converter);
    }

    private static DaoBuilder<ReportCard> reportCardDaoBuilder(){
        return new DaoBuilder<>("REPORT_CARDS", ReportCard::new)
                .withPrimaryKey("ID", "report_card_seq", ReportCard::getId, ReportCard::setId)
                .withStringColumn("NAME", ReportCard::getName, ReportCard::setName)
                .withLongColumn("SESSION_NUMBER", ReportCard::getSessionNumber, ReportCard::setSessionNumber)
                .withParentColumn("ORGANIZATION_ID", ReportCard::getOrganization, ReportCard::setOrganization)
                .withChildren(ReportCard::getReportFactors, ReportCard::setReportFactors, REPORT_FACTORS)
                .withChildren(ReportCard::getReportCardLegislators, ReportCard::setReportCardLegislators, REPORT_CARD_LEGISLATORS)
                .withChildren(ReportCard::getGradeLevelList, ReportCard::setGradeLevelList, GRADE_LEVELS);
    }

    private static DaoBuilder<BillActionLoad> billActionLoadDaoBuilder(){
        return new DaoBuilder<>("BILL_ACTION_LOADS", BillActionLoad::new)
                .withPrimaryKey("ID", "bill_action_load_seq", BillActionLoad::getId, BillActionLoad::setId)
                .withInstantColumn("LOAD_TIME", BillActionLoad::getLoadInstant, BillActionLoad::setLoadInstant)
                .withStringColumn("URL", BillActionLoad::getUrl, BillActionLoad::setUrl)
                .withLongColumn("CHECK_SUM", BillActionLoad::getCheckSum, BillActionLoad::setCheckSum)
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

    private static DaoBuilder<Organization> organizationDaoBuilder(){
        return new DaoBuilder<>("ORGANIZATIONS", Organization::new)
                .withPrimaryKey("ID", "organization_seq", Organization::getId, Organization::setId)
                .withStringColumn("NAME", Organization::getName, Organization::setName)
                .withChildren(Organization::getReportCards, Organization::setReportCards, REPORT_CARDS);
    }

    private static DaoBuilder<User> userDaoBuilder(){
        return new DaoBuilder<>("USERS", User::new)
                .withPrimaryKey("ID", "user_seq", User::getId, User::setId)
                .withStringColumn("EMAIL", User::getEmail, User::setEmail)
                .withStringColumn("SALT", User::getSalt, User::setSalt)
                .withStringColumn("PASSWORD", User::getPassword, User::setPassword);
    }

    private static AssociationDaoBuilder<User, Organization> userOrganizationAssociationDaoBuilder(){
        return new AssociationDaoBuilder<>(USERS, ORGANIZATIONS)
                .withPrimaryKeyName("id")
                .withSequenceName("user_organization_association_seq")
                .withTableName("user_organization_associations")
                .withLeftColumnName("user_id")
                .withRightColumnName("organization_id");
    }

    private static DaoBuilder<CommitteeMember> committeeMemberDaoBuilder(){
        return new DaoBuilder<>("COMMITTEE_MEMBERS", CommitteeMember::new)
                .withPrimaryKey("ID", "COMMITTEE_MEMBER_SEQ", CommitteeMember::getId, CommitteeMember::setId)
                .withParentColumn("COMMITTEE_ID")
                .withStringColumn("TITLE", CommitteeMember::getTitle, CommitteeMember::setTitle).notNull()
                .withJoinColumn("LEGISLATOR_ID", CommitteeMember::getLegislator, CommitteeMember::setLegislator, LEGISLATORS).notNull();
    }

    private static DaoBuilder<Committee> committeeDaoBuilder(){
        return new DaoBuilder<>("COMMITTEE", Committee::new)
                .withPrimaryKey("ID", "COMMITTEE_SEQ", Committee::getId, Committee::setId)
                .withChildren(Committee::getMembers, Committee::setMembers, COMMITTEE_MEMBERS)
                .withStringColumn("NAME", Committee::getName, Committee::setName).notNull()
                .withStringColumn("CODE", Committee::getCode, Committee::setCode).notNull()
                .withStringColumn("COMMITTEE_ID", Committee::getCommitteeId, Committee::setCommitteeId).notNull()
                .withConvertingStringColumn("CHAMBER", Committee::getChamber, Committee::setChamber, Chamber.Converter).notNull();
    }
}
