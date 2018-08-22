-- DESTRUCTIVE SCRIPT
-- this deletes all data 

begin;

delete from votes;

delete from vote_loads;

delete from legislators;

delete from report_factors;

delete from report_cards;

delete from bills;

end;

