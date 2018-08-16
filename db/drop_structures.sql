-- DESTRUCTIVE SCRIPT
-- this deletes all data and drops all structures

begin;

delete from votes;
drop table votes;
drop sequence vote_seq;

delete from legislators;
drop table legislators;
drop sequence legislator_seq;

delete from bills;
drop table bills;
drop sequence bill_seq;

end;

