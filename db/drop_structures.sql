-- DESTRUCTIVE SCRIPT
-- this deletes all data and drops all structures

begin;

delete from bill_actions;
drop table bill_actions;
drop sequence bill_action_seq;

delete from bill_action_loads;
drop table bill_action_loads;
drop sequence bill_action_load_seq;

delete from legislators;
drop table legislators;
drop sequence legislator_seq;

delete from report_factors;
drop table report_factors;
drop sequence report_factor_seq;

delete from report_cards;
drop table report_cards;
drop sequence report_card_seq;

delete from bills;
drop table bills;
drop sequence bill_seq;

end;

