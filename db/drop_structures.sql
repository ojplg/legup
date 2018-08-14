
begin;

delete from persons;
drop table persons;
drop sequence person_seq;

drop table legislators;
drop sequence legislator_seq;

drop table bills;
drop sequence bill_seq;

drop table votes;
drop sequence vote_seq;

end;

