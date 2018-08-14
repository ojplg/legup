
begin;

create sequence person_seq start 1;

create table persons (
    id integer PRIMARY KEY,
    prefix text,
    first_name text,
    middle_name text,
    last_name text,
    suffix text
);

create sequence legislator_seq start 1;

create table legislators (
    id integer PRIMARY KEY,
    party text,
    assembly text,
    year integer,
    person_id integer
);

create sequence bill_seq start 1;

create table bills (
    id integer PRIMARY KEY,
    bill_number integer not null,
    assembly text
);

create sequence vote_seq start 1;

create table votes (
    id integer PRIMARY KEY,
    bill_id integer not null,
    legislator_id integer not null,
    code text not null
);    

end;

