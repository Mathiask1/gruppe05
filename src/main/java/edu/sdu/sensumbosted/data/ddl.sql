-- auto-generated definition
create table department
(
    id   uuid not null
        constraint department_pk
            primary key,
    name text not null
);

alter table department
    owner to kjpusdpl;

create unique index department_id_uindex
    on department (id);

-- auto-generated definition
create table "user"
(
    id         uuid not null
        constraint user_pk
            primary key,
    department uuid,
    name       text,
    auth       smallint
);

alter table managers
    owner to kjpusdpl;

create unique index user_id_uindex
    on managers (id);

