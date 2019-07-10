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

--changeset ojplg:2

alter table bill_actions add constraint uniq_bill_action unique (bill_id, legislator_id, bill_action_type);

--changeset ojplg:3

create sequence organization_seq start 1;

create table organizations (
    id integer PRIMARY KEY,
    name text not null
);

create sequence user_seq start 1;

create table users (
    id integer PRIMARY KEY,
    email text not null,
    salt text not null,
    password text not null,
    organization_id integer not null
);

alter table users add foreign key (organization_id) references organizations(id);

alter table report_cards add organization_id integer not null;

alter table report_cards add foreign key (organization_id) references organizations(id);

--changeset ojplg:4

create sequence grade_level_seq start 1;

create table grade_levels (
    id integer PRIMARY KEY,
    report_card_id integer not null,
    grade text not null,
    percentage integer not null
);

alter table grade_levels add foreign key (report_card_id) references report_cards(id);

alter table grade_levels add constraint uniq_grade_level unique (report_card_id, grade);

-- changeset ojplg:5

alter table report_cards drop constraint if exists uniq_report_card;
alter table report_cards add constraint uniq_report_card unique (name, organization_id);

-- changeset ojplg:6

alter table grade_levels drop constraint uniq_grade_level;

alter table grade_levels add chamber text;

alter table grade_levels add constraint uniq_grade_level unique (report_card_id, chamber, grade);

-- changeset ojplg:7

alter table bills add sub_type text;

update bills set sub_type = 'Bill';

alter table bills alter column sub_type set not null;
alter table bills drop constraint uniq_bill;
alter table bills add constraint uniq_bill unique (session_number, bill_number, chamber, sub_type);

-- changeset ojplg:8

alter table users drop column organization_id;

create table user_organization_associations (
    id integer primary key,
    user_id integer not null,
    organization_id integer not null
);

create sequence user_organization_association_seq start 1;

-- changeset ojplg:9

alter table user_organization_associations add foreign key (organization_id) references organizations(id);
alter table user_organization_associations add foreign key (user_id) references users(id);

-- changeset ojplg:10

--alter table bill_actions add column action_date timestamp;
--alter table bill_actions add column bill_action_type_detail text;

--alter table bill_actions drop constraint uniq_bill_action;
--alter table bill_actions add constraint uniq_bill_action unique (bill_id, legislator_id, bill_action_type, bill_action_type_detail);
