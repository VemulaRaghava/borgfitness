# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table exercise (
  name                      varchar(255) not null,
  minutes                   integer,
  constraint pk_exercise primary key (name))
;

create sequence exercise_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists exercise;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists exercise_seq;

