
begin;

create sequence person_seq start 1;

create table persons (
    id integer PRIMARY KEY DEFAULT nextval('person_seq'),
    prefix text,
    first_name text,
    middle_name text,
    last_name text,
    suffix text
);

grant all on sequence person_seq to legupuser;
grant all on table persons to legupuser;

create sequence legislator_seq start 1;

create table legislators (
    id integer PRIMARY KEY DEFAULT nextval('legislator_seq'),
    district integer,
    party text,
    assembly text,
    year integer,
    person_id integer REFERENCES persons (id)
);

grant all on sequence legislator_seq to legupuser;
grant all on table legislators to legupuser;

create sequence bill_seq start 1;

create table bills (
    id integer PRIMARY KEY,
    bill_number integer not null,
    assembly text
);

grant all on sequence bill_seq to legupuser;
grant all on table bills to legupuser;

create sequence vote_seq start 1;

create table votes (
    id integer PRIMARY KEY,
    bill_id integer REFERENCES bills (id),
    legislator_id integer REFERENCES legislators (id),
    code text not null
);    

grant all on sequence vote_seq to legupuser;
grant all on table votes to legupuser;

end;

