-- DESTRUCTIVE SCRIPT
-- this deletes all data 

begin;

delete from bill_actions;

delete from bill_action_loads;

delete from legislators;

delete from report_factors;

delete from report_cards;

delete from bills;

end;

