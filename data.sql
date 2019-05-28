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

alter table audit owner to CURRENT_USER;

create unique index audit_id_uindex
    on audit (id);

create table departments
(
    id   uuid not null
        constraint department_pk
            primary key,
    name text not null
);

alter table departments owner to CURRENT_USER;

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

alter table managers owner to CURRENT_USER;

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

alter table patients owner to CURRENT_USER;

create unique index patient_id_uindex
    on patients (id);

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

alter table practitioners owner to CURRENT_USER;

create unique index practitioner_id_uindex
    on practitioners (id);

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

alter table practitionerpatientrelation owner to CURRENT_USER;

-----------------
-- Row inserts --
-----------------

INSERT INTO public.departments VALUES ('cb995417-06f4-4ef5-94c1-baf95f536ae3', 'EG Team Online');
INSERT INTO public.departments VALUES ('8f32cc01-7c50-4115-b13e-dfd48c09b391', 'SDU Odense');
INSERT INTO public.departments VALUES ('8153387c-c047-46ce-87f5-c4acf9115baf', 'SDU Kolding');
INSERT INTO public.managers VALUES ('1fdf2bcd-c7ae-4d97-8023-735b96f8900e', 'cb995417-06f4-4ef5-94c1-baf95f536ae3', 'Bob Ross', 'SUPERUSER');
INSERT INTO public.managers VALUES ('f9dc6f0b-dc51-4eb7-b0af-255417d56821', '8f32cc01-7c50-4115-b13e-dfd48c09b391', 'Henrik', 'CASEWORKER');
INSERT INTO public.managers VALUES ('1139ad33-1d74-42e9-9d43-d1ec829a7d97', '8f32cc01-7c50-4115-b13e-dfd48c09b391', 'Anne', 'LOCAL_ADMIN');
INSERT INTO public.managers VALUES ('52f449e5-cfbc-41ca-a119-417ef147f4ee', '8153387c-c047-46ce-87f5-c4acf9115baf', 'Tom', 'LOCAL_ADMIN');
INSERT INTO public.managers VALUES ('0f284af5-bb17-4fe5-9b4d-2679764166a1', '8153387c-c047-46ce-87f5-c4acf9115baf', 'Mikkeline', 'CASEWORKER');
INSERT INTO public.patients VALUES ('1157412c-fbcb-4c04-8565-d89f4cea2609', '8f32cc01-7c50-4115-b13e-dfd48c09b391', 'Mathias', false, '{}', '[]');
INSERT INTO public.patients VALUES ('17d56601-f7ec-4bfa-a310-ec5d5ae5739b', '8153387c-c047-46ce-87f5-c4acf9115baf', 'Patient Zero', false, '{}', '[]');
INSERT INTO public.patients VALUES ('f6757234-3f3c-4851-afbc-121e1f33421f', '8153387c-c047-46ce-87f5-c4acf9115baf', 'Rambo', false, '{}', '[]');
INSERT INTO public.practitioners VALUES ('ac65e3ff-6db0-424a-9d18-506395488ec2', '8f32cc01-7c50-4115-b13e-dfd48c09b391', 'Frederik');
INSERT INTO public.practitioners VALUES ('0b6b6c8c-6e3a-4ba1-a652-0813014db76f', '8f32cc01-7c50-4115-b13e-dfd48c09b391', 'Jack');
INSERT INTO public.practitioners VALUES ('478603ea-55ad-4bab-b716-aa538879304c', '8153387c-c047-46ce-87f5-c4acf9115baf', 'Jonas');
INSERT INTO public.practitioners VALUES ('b6d09679-c9e9-4eb2-8569-05d57bbc951d', '8f32cc01-7c50-4115-b13e-dfd48c09b391', 'Mads');
INSERT INTO public.practitionerpatientrelation VALUES ('b6d09679-c9e9-4eb2-8569-05d57bbc951d', '1157412c-fbcb-4c04-8565-d89f4cea2609');
