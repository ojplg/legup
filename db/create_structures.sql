--liquibase formatted sql

-- script for the creation of structures necessary to support legup
-- Try running
-- psql -f structures.sql legup
-- once the database and user have been created

--changeset ojplg:1

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
    complete_term text,
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


-- VOTE LOADS
create sequence bill_action_load_seq start 1;

create table bill_action_loads (
    id integer PRIMARY KEY,
    load_time timestamp not null,
    bill_id integer not null,
    url text not null,
    check_sum text not null
);

alter table bill_action_loads add foreign key (bill_id) references bills(id);

grant all on sequence bill_action_load_seq to legupuser;
grant all on table bill_action_loads to legupuser;


-- VOTES
create sequence bill_action_seq start 1;

create table bill_actions (
    id integer PRIMARY KEY,
    bill_id integer not null,
    legislator_id integer not null,
    bill_action_load_id integer not null,
    bill_action_type text not null,
    bill_action_detail text
);    

alter table bill_actions add foreign key (bill_id) references bills(id);
alter table bill_actions add foreign key (legislator_id) references legislators(id);
alter table bill_actions add foreign key (bill_action_load_id) references bill_action_loads(id);

grant all on sequence bill_action_seq to legupuser;
grant all on table bill_actions to legupuser;


-- REPORT CARDS
create sequence report_card_seq start 1;

create table report_cards (
    id integer PRIMARY KEY,
    name text not null,
    session_number integer not null
);

alter table report_cards add constraint uniq_report_card unique (name);

grant all on sequence report_card_seq to legupuser;
grant all on table report_cards to legupuser;


-- REPORT FACTORS
create sequence report_factor_seq start 1;

create table report_factors (
    id integer PRIMARY KEY,
    report_card_id integer not null,
    bill_id integer not null,
    vote_side text not null
);

--alter table report_factors add foreign key (report_card_id) references report_cards(id);
alter table report_factors add foreign key (bill_id) references bills(id);
alter table report_factors add constraint uniq_report_factor unique (report_card_id, bill_id);

grant all on sequence report_factor_seq to legupuser;
grant all on table report_factors to legupuser;


-- REPORT CARD LEGISLATORS
create sequence report_card_legislator_seq start 1;

create table report_card_legislators (
    id integer PRIMARY KEY,
    report_card_id integer not null,
    legislator_id integer not null
);

alter table report_card_legislators add foreign key (report_card_id) references report_cards(id);
alter table report_card_legislators add foreign key (legislator_id) references legislators(id);

grant all on sequence report_card_legislator_seq to legupuser;
grant all on table report_card_legislators to legupuser;

end;

