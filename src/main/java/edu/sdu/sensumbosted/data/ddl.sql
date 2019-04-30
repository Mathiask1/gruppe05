create table if not exists departments
(
    id uuid not null
        constraint department_pk
            primary key,
    name text not null
);

create table if not exists managers
(
    id uuid not null
        constraint manager_pk
            primary key,
    department uuid
        constraint manager_department_id_fk
            references departments,
    name text not null,
    auth text default 'NO_AUTH'::text not null
);

create unique index manager_id_uindex
    on managers (id);

create unique index department_id_uindex
    on departments (id);

create table if not exists practitioners
(
    id uuid not null
        constraint practitioner_pk
            primary key,
    department uuid
        constraint practitioner_department_id_fk
            references departments,
    name text not null
);

create unique index practitioner_id_uindex
    on practitioners (id);

create table if not exists patients
(
    id uuid not null
        constraint patient_pk
            primary key,
    department uuid
        constraint patient_department_id_fk
            references departments,
    name text not null,
    enrolled boolean default false not null,
    diary json not null,
    calendar json not null
);

create unique index patient_id_uindex
    on patients (id);

create table if not exists practitionerpatientrelation
(
    practitioner uuid not null
        constraint practitionerpatientrelation_practitioners_id_fk
            references practitioners
            on delete cascade,
    patient uuid not null
        constraint practitionerpatientrelation_patients_id_fk
            references patients
            on delete cascade
);

create table if not exists audit
(
    id serial not null
        constraint audit_pk
            primary key,
    time time not null,
    action text not null,
    description text not null,
    actor uuid not null
);

create unique index audit_id_uindex
    on audit (id);

