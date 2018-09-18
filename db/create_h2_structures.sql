-- script for the creation of structures necessary to support legup
-- Try running
-- psql -f structures.sql legup
-- once the database and user have been created

begin;

-- LEGISLATORS
create sequence legislator_seq start 1;

create table legislators (
    id integer PRIMARY KEY,
    first_name text,
    middle_name_or_initial text,
    last_name text,
    suffix text,
    district integer not null,
    chamber text not null,
    party text,
    session_number integer not null,
    member_id text
);

grant all on sequence legislator_seq to legupuser;
grant all on table legislators to legupuser;


-- BILLS
create sequence bill_seq start 1;

create table bills (
    id integer PRIMARY KEY,
    bill_number integer not null,
    chamber text not null,
    session_number integer not null,
    short_description text
);

alter table bills add constraint uniq_bill unique (session_number, bill_number, chamber);

grant all on sequence bill_seq to legupuser;
grant all on table bills to legupuser;

end;


