-- script for the creation of structures necessary to support legup
-- Try running
-- psql -f structures.sql legup
-- once the database and user have been created

begin;

create sequence legislator_seq start 1;

create table legislators (
    id integer PRIMARY KEY DEFAULT nextval('legislator_seq'),
    first_name text,
    middle_name_or_initial text,
    last_name text,
    suffix text,
    district integer not null,
    chamber text not null,
    party text,
    session_number integer not null
);

grant all on sequence legislator_seq to legupuser;
grant all on table legislators to legupuser;

create sequence bill_seq start 1;

create table bills (
    id integer PRIMARY KEY DEFAULT nextval('bill_seq'),
    bill_number integer not null,
    chamber text not null,
    UNIQUE (bill_number, chamber)
);

grant all on sequence bill_seq to legupuser;
grant all on table bills to legupuser;

create sequence vote_load_seq start 1;

create table vote_loads (
    id integer PRIMARY KEY DEFAULT nextval('vote_load_seq'),
    load_time timestamp not null,
    bill_id integer REFERENCES bills (id),
    url text not null,
    checkSum text not null
);

grant all on sequence vote_load_seq to legupuser;
grant all on table vote_loads to legupuser;

create sequence vote_seq start 1;

create table votes (
    id integer PRIMARY KEY DEFAULT nextval('vote_seq'),
    bill_id integer REFERENCES bills (id),
    legislator_id integer REFERENCES legislators (id),
    vote_load_id integer REFERENCES vote_loads (id),
    vote_side text not null
);    

grant all on sequence vote_seq to legupuser;
grant all on table votes to legupuser;

end;

