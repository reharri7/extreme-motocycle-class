drop database if exists `extreme-motorcycle-class`;
create database if not exists `extreme-motorcycle-class`;
use `extreme-motorcycle-class`;

drop table if exists range_assignment;
drop table if exists coach_assignment;
drop table if exists bike_assignment;
drop table if exists course_schedule;
drop table if exists time_type;
drop table if exists course_enrollment;
drop table if exists student;
drop table if exists bike_range;
drop table if exists range_type;
drop table if exists problem;
drop table if exists coach;
drop table if exists person;
drop table if exists course;
drop table if exists course_type;
drop table if exists classroom;
drop table if exists bike;
drop table if exists bike_type;

create table if not exists bike_type
(
    bike_type_id    int AUTO_INCREMENT not null
        primary key,
    bike_type_value varchar(10) null
);

create table if not exists bike
(
    bike_id       int	AUTO_INCREMENT not null
        primary key,
    brand         varchar(30) null,
    type          int         not null,
    license_plate varchar(30) not null,
    vin           varchar(30) not null,
    broken        tinyint  null,
    cc            int         null,
    constraint bike_bike_type_bike_type_id_fk
        foreign key (type) references bike_type (bike_type_id)
        on delete cascade
);

create table if not exists classroom
(
    classroom_id int	AUTO_INCREMENT not null
        primary key,
    capacity     int        not null
);

create table if not exists course_type
(
    course_type_id    int	AUTO_INCREMENT not null
        primary key,
    course_type_value varchar(30) not null
);

create table if not exists course
(
    course_id          int	AUTO_INCREMENT not null
        primary key,
    course_name        varchar(30)  null,
    course_type        int          not null,
    capacity           int          null,
    course_description varchar(100) null,
    cost               int          null,
    constraint course_course_type_course_type_id_fk
        foreign key (course_type) references course_type (course_type_id)
        on delete cascade
);

create table if not exists person
(
    person_id  int	AUTO_INCREMENT not null
        primary key,
    full_name  varchar(30)  null,
    address    varchar(150) null,
    date_birth date         null,
    phone      varchar(10)  null
);

create table if not exists coach
(
    coach_id             int	AUTO_INCREMENT not null
        primary key,
    person_id            int        not null,
    classroom_certified  tinyint null,
    dirtbike_certified   tinyint null,
    streetbike_certified tinyint null,
    constraint coach_person_person_id_fk
        foreign key (person_id) references person (person_id) on delete cascade
);

create table if not exists problem
(
    problem_id   int	AUTO_INCREMENT not null
        primary key,
    problem_date date         not null,
    bike_id      int          not null,
    repair_date  date         null,
    description  varchar(150) null,
    cost         int          null,
    constraint problem_bike_bike_id_fk
        foreign key (bike_id) references bike (bike_id) on delete cascade
);

create table if not exists range_type
(
    range_type_id    int	AUTO_INCREMENT not null
        primary key,
    range_type_value varchar(30) not null
);

create table if not exists bike_range
(
    range_id   int	AUTO_INCREMENT not null
        primary key,
    range_type int not null,
    capacity int not null,
    constraint bike_range_range_type_range_type_id_fk
        foreign key (range_type) references range_type (range_type_id) on delete cascade
);

create table if not exists student
(
    student_id int	AUTO_INCREMENT not null
        primary key,
    person_id  int not null,
    constraint student_person_person_id_fk
        foreign key (person_id) references person (person_id) on delete cascade
);

create table if not exists course_enrollment
(
    course_enrollment_id int	AUTO_INCREMENT not null
        primary key,
    course_id            int        null,
    exercise_1_score     int        null,
    exercise_2_score     int        null,
    exercise_3_score     int        null,
    exercise_4_score     int        null,
    exercise_5_score     int        null,
    passed               tinyint null,
    written_score        int        null,
    paid                 tinyint not null default false,
    student_id           int        null,
    constraint course_enrollment_course_course_id_fk
        foreign key (course_id) references course (course_id) on delete set null,
    constraint course_enrollment_student_student_id_fk
        foreign key (student_id) references student (student_id) on delete set null
);

create table if not exists time_type
(
    time_type_id    int	AUTO_INCREMENT not null
        primary key,
    time_type_value varchar(30) not null
);

create table if not exists course_schedule
(
    course_schedule_id int	AUTO_INCREMENT not null
        primary key,
    classroom_id       int  null,
    course_date        date not null,
    course_id          int  not null,
    time_type_id       int  not null,
    constraint course_schedule_classroom_classroom_id_fk
        foreign key (classroom_id) references classroom (classroom_id) on delete cascade,
    constraint course_schedule_course_course_id_fk
        foreign key (course_id) references course (course_id) on delete cascade,
    constraint course_schedule_time_type_time_type_id_fk
        foreign key (time_type_id) references time_type (time_type_id) on delete cascade
);

create table if not exists bike_assignment
(
    bike_assignment_id int	AUTO_INCREMENT not null primary key,
	course_schedule_id	int	not null,
	bike_id				int	not null,
	constraint bike_course_schedule_id_fk
		foreign key (course_schedule_id) references course_schedule (course_schedule_id) on delete cascade,
	constraint bike_id_fk
		foreign key (bike_id) references bike (bike_id) on delete cascade
);

create table if not exists coach_assignment
(
    coach_assignment_id int	AUTO_INCREMENT not null primary key,
	course_schedule_id	int	not null,
	coach_id			int not null,
	assigned_role		varchar(30) null,
	constraint coach_course_schedule_id_fk
		foreign key (course_schedule_id) references course_schedule (course_schedule_id) on delete cascade,
	constraint coach_id_fk
		foreign key (coach_id) references coach (coach_id) on delete cascade
);

create table if not exists range_assignment
(
    range_assignment_id int	AUTO_INCREMENT not null primary key,
	range_id	int	not null,
	course_schedule_id	int	not null,
	constraint range_course_schedule_id_fk
		foreign key (course_schedule_id) references course_schedule (course_schedule_id) on delete cascade,
	constraint range_id_fk
		foreign key (range_id) references bike_range (range_id) on delete cascade
);
