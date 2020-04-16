alter table ENV_FEATURE drop constraint ENV_FEATURE_TO_ENV_FK;
alter table ENV_FEATURE drop constraint ENV_FEATURE_TO_FEATURE_FK;
alter table ENV_MICROSERVICE drop constraint MICROSERVICE_TO_ENV_FK;
alter table REPO_COMMIT drop constraint COMMIT_TO_FEATURE_FK;
alter table REPO_COMMIT drop constraint COMMIT_TO_REPO_FK;
drop table if exists ENV cascade;
drop table if exists ENV_FEATURE cascade;
drop table if exists ENV_MICROSERVICE cascade;
drop table if exists FEATURE cascade;
drop table if exists REPO cascade;
drop table if exists REPO_COMMIT cascade;
drop sequence ENV_FEATURE_SEQ;
drop sequence ENV_MICROSERVICE_ID_SEQ;
drop sequence REPO_COMMIT_ID_SEQ;
