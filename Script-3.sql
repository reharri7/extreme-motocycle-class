create table bike(
	bike_id int not null,
	brand varchar(30),
	type varchar(30),
	license_plate varchar(30),
	vin varchar(30),
	broken boolean,
	cc???);

create table problem
	problem_date varchar(10) not null,
	bike_id int,
	repair_date varchar(30),
	description varchar(150),
	cost int);

create table classroom
	classroom_id int not null,
	capacity int,
	available boolean);

create table range
	range_id int not null,
	range_type varchar(30),
	available boolean);

create table course(
	course_id int not null,
	name varchar(30),
	course_type varchar(30),
	capacity int,
	description varchar(150),
	cost int,
	date varchar(10));

create table coach(
	person_id int not null,
	classroom_certified boolean,
	dirtbike_certified boolean,
	streetbike_certified boolean);

create table student(
	person_id int not null);

create table person(
	person_id int not null,
	name varchar(30),
	address varchar(150),
	date_birth varchar(10),
	phone varchar(10));
	
create table courseSchedule(
	coach_id int,
	classroom_id int,
	range_id int,
	date varchar