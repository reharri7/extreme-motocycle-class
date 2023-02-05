use `extreme-motorcycle-class`;
insert into `bike_type` values (1, 'Street'), (2,'Dirt');
insert into `bike` values (1, 'Toyota', 1, '555ABC', '1111abcd1111abcd1', false, 250), (2, 'Ford', 2, '555ABD', '1111abcd1111abcd2', false, 250), (3, 'Toyota', 1, '555ABE', '1111abcd1111abcd3', false, 250), (4, 'Toyota', 1, '555ABF', '1111abcd1111abcd4', true, 250), (5, 'Ford', 2, '555ABG', '1111abcd1111abcd5', false, 250);
insert into `classroom` values (1, 15), (2,30);
insert into `course_type` values (1, 'Street'), (2, 'Dirt');
insert into `course` values (1, 'Dirt Bike Basics', 2, 15, 'Basic dirt bike skills', 200), (2, 'Basic Motorcycle Skills', 1, 30, 'Basic street bike skills', 200), (3, 'Advanced Motorcycle Skills', 1, 30, 'Advanced street bike skills', 400);
insert into `person` values (1, 'Maura Peterson', '111 1st street', '1995-01-01', '5555555555'), (2, 'Josh McManus', '222 2nd street', '1996-01-01', '5555555556'), (3, 'Rhett Harrison', '333 3rd street', '1997-01-01', '5555555557'), (4, 'Addison Corey', '444 4th street', '1998-01-01', '5555555558'), (5, 'Muhammad Fateen', '555 5th street', '1999-01-01', '5555555559');
insert into `coach` values (1, 1, 1, 0, 1), (2, 2, 0, 1, 0);
insert into `problem` values (1, '2023-01-01', 1, '2023-01-02', 'wheel fell off', 100), (2, '2023-01-03', 1, '2023-01-04', 'wheel fell off', 100), (3, '2023-01-01', 2, '2023-01-02', ' both wheels fell off', 200);
insert into `range_type` values (1, 'Street'), (2,'Dirt');
insert into `bike_range` values (1, 2, 15), (2, 1, 15), (3, 1, 15), (4, 1, 15);
insert into `student` values (1, 3), (2,4), (3,5);
insert into `course_enrollment` values (1, 1, 100, 100, 100, 100, 100, 1, 100, 1, 1), (2, 2, 100, 100, 100, 100, 100, 21, 100, 1, 2), (3, 3, 100, 100, 100, 100, 100, 1, 100, 1, 3);
insert into `time_type` values (1, 'AM classroom'), (2, 'PM classroom'), (3, 'Full day range');
insert into `course_schedule` values (1, null, 1, '2023-03-01', 1, 3), (2, 1, 1, '2023-03-02', 2, 1), (3, 2, 1, '2023-03-03', 3,2);
insert into `bike_assignment` values (1,1,2), (2,1,5), (3,2,1), (4,2,3), (5,2,4), (6,3,1), (7,3,3), (8,3,4);
insert into `coach_assignment` values (1, 1, 2, "Range"), (2, 2, 1, "Classroom"), (3, 2, 1, "Range"), (4, 3, 1, "Classroom"), (5, 3, 1, "Range");