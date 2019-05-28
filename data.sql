-- Includes all from ddl.sql but also contains row inserts

create table audit
(
    id          serial    not null
        constraint audit_pk
            primary key,
    action      text,
    description text      not null,
    actor       uuid,
    time        timestamp not null
);

alter table audit
    owner to kjpusdpl;

create unique index audit_id_uindex
    on audit (id);

create table departments
(
    id   uuid not null
        constraint department_pk
            primary key,
    name text not null
);

alter table departments
    owner to kjpusdpl;

create unique index department_id_uindex
    on departments (id);

create table managers
(
    id         uuid                         not null
        constraint manager_pk
            primary key,
    department uuid
        constraint manager_department_id_fk
            references departments,
    name       text                         not null,
    auth       text default 'NO_AUTH'::text not null
);

alter table managers
    owner to kjpusdpl;

create unique index manager_id_uindex
    on managers (id);

create table patients
(
    id         uuid                  not null
        constraint patient_pk
            primary key,
    department uuid
        constraint patient_department_id_fk
            references departments,
    name       text                  not null,
    enrolled   boolean default false not null,
    diary      json                  not null,
    calendar   json                  not null
);

alter table patients
    owner to kjpusdpl;

create unique index patient_id_uindex
    on patients (id);

create table practitionerpatientrelation
(
    practitioner uuid not null
        constraint practitionerpatientrelation_practitioners_id_fk
            references practitioners
            on delete cascade,
    patient      uuid not null
        constraint practitionerpatientrelation_patients_id_fk
            references patients
            on delete cascade
);

alter table practitionerpatientrelation
    owner to kjpusdpl;

create table practitioners
(
    id         uuid not null
        constraint practitioner_pk
            primary key,
    department uuid
        constraint practitioner_department_id_fk
            references departments,
    name       text not null
);

alter table practitioners
    owner to kjpusdpl;

create unique index practitioner_id_uindex
    on practitioners (id);

-----------------
-- Row inserts --
-----------------


