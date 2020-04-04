

-- MySQL
-- DROP TABLE IF EXISTS objectid;
-- CREATE TABLE objectid (id INT NOT NULL);
-- INSERT INTO objectid VALUES (0);
-- Postgres
DROP SEQUENCE IF EXISTS objectid;
CREATE SEQUENCE objectid  INCREMENT 16  MINVALUE 1  MAXVALUE 9223372036854775807  START 10000  CACHE 1;

DROP TABLE IF EXISTS master_user;
DROP TABLE IF EXISTS usertable;

DROP TABLE IF EXISTS master_group;
DROP TABLE IF EXISTS grouptable;

DROP TABLE IF EXISTS master_policy;
DROP TABLE IF EXISTS policy;	
DROP TABLE IF EXISTS rule;

DROP TABLE IF EXISTS master_app;
DROP TABLE IF EXISTS app;		

DROP TABLE IF EXISTS master_machine;
DROP TABLE IF EXISTS machine;		
	
DROP TABLE IF EXISTS user2group;
DROP TABLE IF EXISTS app2group;
DROP TABLE IF EXISTS machine2group;
DROP TABLE IF EXISTS user2policy;
DROP TABLE IF EXISTS rule2policy;
	

CREATE TABLE master_user (mid bigint, birthday timestamp, primary key(mid));
CREATE TABLE usertable (sid bigint, mid bigint, first_name varchar(128), last_name varchar(128), login_name varchar(256), password varchar(256), department varchar(256), note varchar (512), active smallint, status smallint, version timestamp, primary key(sid));

CREATE TABLE master_group (mid bigint, type varchar(32), birthday timestamp, primary key(mid));
CREATE TABLE grouptable (sid bigint, mid bigint, name varchar(128), description varchar(512), status smallint, version timestamp, primary key(sid));

CREATE TABLE master_policy (mid bigint, type varchar(32), action varchar(32), birthday timestamp, primary key(mid));
CREATE TABLE policy (sid bigint, mid bigint, name varchar(128), description varchar(512), status smallint, version timestamp default current_timestamp, primary key(sid));
CREATE TABLE rule (sid bigint, name varchar (128), description varchar(512), criteria varchar(1024), birthday timestamp, primary key(sid));
	

CREATE TABLE master_app (mid bigint, parent_mid bigint, type varchar(32), max_copy smallint, birthday timestamp, deathday timestamp, primary key(mid));
CREATE TABLE app (sid bigint, mid bigint, name varchar(128), description varchar(512), owner bigint, status smallint, version timestamp, primary key(sid));

CREATE TABLE master_machine (mid bigint, type varchar(32), birthday timestamp, primary key(mid));
CREATE TABLE machine (sid bigint, mid bigint, name varchar(128), description varchar(512), owner bigint, status smallint, version timestamp, primary key(sid));

CREATE TABLE user2group (sid bigint, user_mid bigint, group_mid bigint, type varchar(32), status smallint, version timestamp, primary key(sid));
CREATE TABLE app2group (sid bigint, app_mid bigint, group_mid bigint, type varchar(32), status smallint, version timestamp, primary key(sid));
CREATE TABLE machine2group (sid bigint, machine_mid bigint, group_mid bigint, type varchar(32), status smallint, version timestamp, primary key(sid));
CREATE TABLE user2policy (sid bigint, user_mid bigint, policy_mid bigint, type varchar(32), status smallint, version timestamp, primary key(sid));
CREATE TABLE rule2policy (sid bigint, rule_sid bigint, policy_sid bigint, version timestamp, primary key(sid));

-- Metadata for expression operand and object attributes
DROP TABLE IF EXISTS meta_operand;
CREATE TABLE meta_operand (attr_name varchar(64), attr_ui_name varchar(64), attr_db_name varchar(64), attr_default_value varchar(1024), attr_data_type varchar(32), attr_selection_type varchar(8), status smallint, birthday timestamp, primary key(attr_name));

INSERT INTO meta_operand (attr_name, attr_ui_name, attr_default_value, attr_data_type, attr_selection_type, attr_db_name, status, birthday) 
VALUES ('$User.Active', 'active', 'false', 'Integer', 'SS', 'usertable.active', 0, now());

INSERT INTO meta_operand (attr_name, attr_ui_name, attr_default_value, attr_data_type, attr_selection_type, attr_db_name, status, birthday) 
VALUES ('$User.Status', 'status', '0', 'Integer', 'SS', 'usertable.status', 0, now());


-- Metadata for expression operator 

DROP TABLE IF EXISTS meta_operator;
CREATE TABLE meta_operator (operator_name varchar(512), data_type varchar(64), selection_type varchar(8),  operator_class_name varchar(512), operator_method_name varchar(64), operand_number smallint, operator_ui_name varchar(32), operator_db_name varchar(64), status smallint, birthday timestamp, primary key(operator_name, data_type, selection_type));



-- Logical Op
INSERT INTO meta_operator (operator_name, data_type, selection_type, operator_class_name, operator_method_name, operand_number, operator_ui_name, operator_db_name, status, birthday) 
VALUES ('And', 'Boolean', 'BL', 'com.fusui.tapir.service.acs.pdp.operator.BooleanOperator', 'And', 2, 'And', 'and', 0, now());

INSERT INTO meta_operator (operator_name, data_type, selection_type, operator_class_name, operator_method_name, operand_number, operator_ui_name, operator_db_name, status, birthday) 
VALUES ('Or', 'Boolean', 'BL', 'com.fusui.tapir.service.acs.pdp.operator.BooleanOperator', 'Or', 2, 'Or', 'or', 0, now());

INSERT INTO meta_operator (operator_name, data_type, selection_type, operator_class_name, operator_method_name, operand_number, operator_ui_name, operator_db_name, status, birthday) 
VALUES ('Not', 'Boolean', 'BL', 'com.fusui.tapir.service.acs.pdp.operator.BooleanOperator', 'Not', 1, 'Not', 'not', 0, now());

-- Integer
INSERT INTO meta_operator (operator_name, data_type, selection_type, operator_class_name, operator_method_name, operand_number, operator_ui_name, operator_db_name, status, birthday) 
VALUES ('Equal', 'Integer', 'SS', 'com.fusui.tapir.service.acs.pdp.operator.IntegerOperator', 'Equal', 2, 'Equal', '=', 0, now());

INSERT INTO meta_operator (operator_name, data_type, selection_type, operator_class_name, operator_method_name, operand_number, operator_ui_name, operator_db_name, status, birthday) 
VALUES ('NotEqual', 'Integer', 'SS', 'com.fusui.tapir.service.acs.pdp.operator.IntegerOperator', 'NotEqual', 2, 'NotEqual', '!=', 0, now());

-- String
INSERT INTO meta_operator (operator_name, data_type, selection_type, operator_class_name, operator_method_name, operand_number, operator_ui_name, operator_db_name, status, birthday) 
VALUES ('Equal', 'String', 'TF', 'com.fusui.tapir.service.acs.pdp.operator.StringOperator', 'Equal', 2, 'Equal', '=', 0, now());
