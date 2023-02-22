package emc;

/**
 * StudentMenu.java
 *
 * Prints and handles Student sub menu.
 *
 * Group 4
 */

import java.sql.*;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.time.LocalDate;

import static emc.Utils.createLocalDate;
import static emc.Utils.printSet;


public class StudentMenu {

    private static final String CREATE_PERSON = "INSERT INTO person (full_name, address, date_birth, phone) VALUES (?, ?, ?, ?)";
    private static final String SELECT_PERSON_BY_ID = "SELECT person_id FROM person WHERE full_name=? AND address=? AND date_birth=? AND phone=?";
    private static final String CREATE_STUDENT = "INSERT INTO student (personId) VALUES (?)";
    private static final String VIEW_STUDENTS = "SELECT student.student_id,person.full_name FROM student,person";
    private static final String SELECT_STUDENT = "SELECT person_id FROM student WHERE student_id=?";
    private static final String DELETE_PERSON = "DELETE FROM person WHERE person_id=?";
    private static final String DELETE_COURSE_ENROLLMENT = "DELETE FROM course_enrollment WHERE student_id=?";
    private static final String DELETE_STUDENT = "DELETE FROM student WHERE student_id=?";
    private static final String STUDENT_REPORT = "SELECT course_enrollment.course_id, course.course_name, course_enrollment.paid FROM course_enrollment,course WHERE course_enrollment.course_id=course.course_id AND course_enrollment.student_id=?";
    private static final String ENROLL_STUDENT = "INSERT INTO course_enrollment (course_id, student_id) VALUES (?, ?)";
    private static final String UNENROLL_STUDENT = "DELETE FROM course_enrollment WHERE course_enrollment_id=?";
    private static final String VIEW_ENROLLMENT = "SELECT * FROM course_enrollment WHERE course_enrollment_id=?";




    /**
     * Print student menu options and call appropriate method.
     * @param rs result set
     * @param ps prepared statement
     * @param conn connection
     * @param scanner scanner
     * @throws SQLException SQL exception
     */
    public void menu(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        int selection;

        while(true) {
            printMenu();
            try {
                selection = scanner.nextInt();
                switch (selection) {
                    case 1 -> manageStudents(rs, ps, conn, scanner);
                    case 2 -> studentEnrollment(rs, ps, conn, scanner);
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Please enter an integer between 0 and 2");
                }

            }catch (InputMismatchException ex){
                System.out.println("Please enter an integer value between 0 and 2");
                scanner.next();
            }
        }
    }

    /**
     * Print manage student sub menu options and call appropriate method.
     * @param rs result set
     * @param ps prepared statement
     * @param conn connection
     * @param scanner scanner
     * @throws SQLException SQL exception
     */
    public void manageStudents(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        int selection;

        while(true) {
            printManageMenu();
            try {
                selection = scanner.nextInt();
                switch (selection) {
                    case 1 -> createStudent(rs, ps, conn, scanner);
                    case 2 -> viewStudents(rs, conn);
                    case 3 -> editStudent(ps, conn, scanner);
                    case 4 -> deleteStudent(rs, ps, conn, scanner);
                    case 5 -> studentReport(rs, ps, conn, scanner);
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Please enter an integer between 0 and 5");
                }

            }catch (InputMismatchException ex){
                System.out.println("Please enter an integer value between 0 and 5");
                scanner.next();
            }
        }
    }

    /**
     * UX will ask for student information and call DB with this query:
     *     Parameter 1: full_name: varchar(30)
     *     Parameter 2: address: varchar(50)
     *     Parameter 3: date_birth: date
     *     Parameter 4: phone: varchar(10)
     * INSERT INTO person (full_name, address, date_birth, phone) VALUES (‘?’, ‘?’, ?, ‘?’);
     * INSERT INTO student (person_id) VALUES (
     *     SELECT person_id
     *     FROM person
     *     WHERE full_name=’?’
     *     AND address=’?’
     *     AND date_birth=?
     *     AND phone=’?’);
     * @param rs result set
     * @param ps prepared statement
     * @param conn connection
     * @param scanner scanner
     */
    public void createStudent(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) {

        int birthMonth;
        int birthDay;
        int birthYear;
        LocalDate dateBirth;


        System.out.print("Please enter a Student name between 1-30 characters: ");
        String student_name = scanner.next();

        System.out.print("Please enter an address between 1-50 characters: ");
        String address = scanner.next();

        try {
            System.out.print("Please enter a numeric birth month value: ");
            birthMonth = scanner.nextInt();

            System.out.print("Please enter birth day: ");
            birthDay = scanner.nextInt();

            System.out.print("Please enter birth year: ");
            birthYear = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Number must be an integer");
            return;
        }

        dateBirth = createLocalDate(birthYear, birthMonth, birthDay);


        System.out.print("Please enter phone number (5555551234): ");
        String phone = scanner.next();

        try {
            conn.setAutoCommit(false); // do not autocommit
            ps = conn.prepareStatement(CREATE_PERSON);
            ps.setString(1, student_name);
            ps.setString(2, address);
            ps.setDate(3, Date.valueOf(dateBirth));
            ps.setString(4, phone);

            int result = ps.executeUpdate();

            if (result > 0) { //success
                ps = conn.prepareStatement(SELECT_PERSON_BY_ID);
                ps.setString(1, student_name);
                ps.setString(2, address);
                ps.setDate(3, Date.valueOf(dateBirth));
                ps.setString(4, phone);

                rs = ps.executeQuery();
                int personId = 0;

                while (rs.next()) {
                    personId = rs.getInt(1);
                }

                if(personId != 0) { //success
                    ps = conn.prepareStatement(CREATE_STUDENT);
                    ps.setInt(1, personId);
                    result = ps.executeUpdate();

                    if (result > 0) { //success
                        System.out.println("Successfully added Student!");
                        conn.commit();
                    } else {
                        System.out.println("Unable to add Student");
                        conn.rollback();
                    }
                } else {
                    // no person_id
                    System.out.println("Unable to find Person");
                    conn.rollback();
                }
            } else {
                System.out.println("Unable to create Person");
                conn.rollback();
            }
        } catch (SQLException sqlx) {
            System.out.println("SQL Exception");
        } finally {
            closeResource(rs, ps, conn, scanner);
        }
    }

    /**
     * UX will call DB with this query:
     * SELECT student.student_id,person.full_name
     * FROM student,person
     * WHERE student.person_id=person.person_id;
     * @param rs result set
     * @param conn connection
     */
    public void viewStudents(ResultSet rs, Connection conn) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(VIEW_STUDENTS);
            printSet(rs);

        } catch (SQLException sqlx) {
            System.out.println("SQL Exception");
        } finally {
            closeResource(rs, conn, stmt);
        }
    }

    /**
     * UX will ask for student id and information to change and call DB with this query:
     *     Parameter 1: student_id:  int
     *     Parameter 2: Value to change
     *     Parameter 3: New value
     *     Parameter X: …
     * UPDATE person
     * SET ?=?
     * FROM person,student
     * WHERE person.person_id=student.person_id AND student.student_id=?;
     * @param ps prepared statement
     * @param conn connection
     * @param scanner scanner
     * @throws SQLException SQL exception
     */
    public void editStudent(PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        conn.setAutoCommit(false); // do not autocommit
        int birthMonth = 0;
        int birthDay = 0;
        int birthYear = 0;
        int studentId;
        boolean cBD = false;
        LocalDate dateBirth = null;

        try {
            System.out.print("Please enter Student ID: ");
            studentId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Number must be an integer");
            return;
        }

        System.out.println("Please enter values to change or leave blank to skip");
        System.out.print("Enter a Student name between 1-30 characters: ");
        String student_name = scanner.next();

        System.out.print("Enter an address between 1-50 characters: ");
        String address = scanner.next();

        try {
            System.out.print("Do you want to change the birthday? Enter 'y' or 'n': ");
            String changeBDay = scanner.next();
            if (changeBDay.equalsIgnoreCase("y")) {
                cBD = true;
                System.out.println("Month, day, and year must be entered to change birthday");
                System.out.print("Enter a numeric birth month value: ");
                birthMonth = scanner.nextInt();

                System.out.print("Enter birth day: ");
                birthDay = scanner.nextInt();

                System.out.print("Enter birth year: ");
                birthYear = scanner.nextInt();
            }
        } catch (InputMismatchException e) {
            System.out.println("Number must be an integer");
            return;
        }

        if (cBD && birthYear != 0 && birthMonth != 0 && birthDay != 0) {
            dateBirth = createLocalDate(birthYear, birthMonth, birthDay);
        }


        System.out.print("Please enter phone number (5555551234): ");
        String phone = scanner.next();

        String statement = "UPDATE person SET ";
        
        boolean name = false;
        boolean addy = false;
        boolean bday = false;
        boolean tele = false;

        if (student_name != null && !student_name.equals("")) {
            statement += "full_name=?,";
            name = true;
        }
        if (address != null && !address.equals("")) {
            statement += "address=?,";
            addy = true;
        }
        if (dateBirth != null) {
            statement += "date_birth=?,";
            bday = true;
        }
        if (phone != null && !phone.equals("")) {
            statement += "phone=?,";
            tele = true;
        }

        if (statement.endsWith(",")) {
            statement = statement.substring(0, statement.length()-1);
        }

        statement += " FROM person,student WHERE person.person_id=student.person_id AND student.studentId=?";

        if (name || addy || bday || tele) {
            try {
                ps = conn.prepareStatement(statement);
                int i = 1;

                if (name) {
                    ps.setString(i, student_name);
                    i++;
                }
                if (addy) {
                    ps.setString(i, address);
                    i++;
                }
                if (bday) {
                    ps.setDate(i, Date.valueOf(dateBirth));
                    i++;
                }
                if (tele) {
                    ps.setString(i, phone);
                    i++;
                }
                ps.setInt(i, studentId);

                int result = ps.executeUpdate();

                if (result > 0) { //success
                    System.out.println("Updated Student!");
                    conn.commit();
                } else {
                    System.out.println("Unable to create Person");
                    conn.rollback();
                }
            } catch (SQLException sqlx) {
                System.out.println("SQL Exception");
            } finally {
                closeResource(ps, conn, scanner);
            }
        } else {
            System.out.println("No values to change");
        }
    }

    /**
     * UX will ask for student id and call DB with this query:
     *     Parameter 1: student_id:  int
     * DELETE FROM person
     * WHERE person_id=(
     *     SELECT person_id
     *     FROM student
     *     WHERE student_id=?);
     * DELETE FROM course_enrollment WHERE student_id=?;
     * DELETE FROM student WHERE student_id=?;
     * @param rs result set
     * @param ps prepared statement
     * @param conn connection
     * @param scanner scanner
     */
    public void deleteStudent(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) {
        int studentId;

        try {
            System.out.print("Please enter Student ID: ");
            studentId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Number must be an integer");
            return;
        }

        try {
            conn.setAutoCommit(false); // do not autocommit
            ps = conn.prepareStatement(SELECT_STUDENT);
            ps.setInt(1, studentId);

            rs = ps.executeQuery();
            int personId = 0;

            while (rs.next()) {
                personId = rs.getInt(1);
            }

            if(personId != 0) { //success, found person
                ps = conn.prepareStatement(DELETE_PERSON);
                ps.setInt(1, personId);
                int result = ps.executeUpdate();

                if (result > 0) { //success, deleted person
//                        System.out.println("Successfully added Student!");
                    ps = conn.prepareStatement(DELETE_COURSE_ENROLLMENT);
                    ps.setInt(1, studentId);
                    result = ps.executeUpdate();

                    if (result > 0) { //success, deleted course_enrollment
                        ps = conn.prepareStatement(DELETE_STUDENT);
                        ps.setInt(1, studentId);
                        result = ps.executeUpdate();

                        if (result > 0) { //success, deleted student
                            System.out.println("Deleted Student!");
                            conn.commit();
                        } else { //failure, delete student
                            System.out.println("Unable to delete Student");
                            conn.rollback();
                        }
                    } else { //failure, delete course_enrollment
                        System.out.println("Unable to delete Course Enrollment");
                        conn.rollback();
                    }
                } else { //failed, delete person
                    System.out.println("Unable to delete Person");
                    conn.rollback();
                }
            } else {
                // no person_id
                System.out.println("Unable to find Person");
                conn.rollback();
            }
        } catch (SQLException sqlx) {
            System.out.println("SQL Exception");
        } finally {
            closeResource(rs, ps, conn, scanner);
        }
    }

    /**
     * UX will ask for student id and call DB with this query:
     *     Parameter 1: student_id:  int
     * SELECT course_enrollment.course_id, course.course_name, course_enrollment.paid
     * FROM course_enrollment,course
     * WHERE course_enrollment.course_id=course.course_id
     * AND course_enrollment.student_id=?;
     * @param rs result set
     * @param ps statement
     * @param conn connection
     * @param scanner scanner
     */
    public void studentReport(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) {
        int studentId;

        try {
            System.out.print("Please enter Student ID: ");
            studentId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Number must be an integer");
            return;
        }

        print(rs, ps, conn, scanner, studentId, STUDENT_REPORT);
    }

    private void print(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner, int Id, String query) {
        try {
            conn.setAutoCommit(false); // do not autocommit
            ps = conn.prepareStatement(query);
            ps.setInt(1, Id);

            rs = ps.executeQuery();
            printSet(rs);

        } catch (SQLException sqlx) {
            System.out.println("SQL Exception");
        } finally {
            closeResource(rs, ps, conn, scanner);
        }
    }

    /**
     * Print student enrollment sub menu options and call appropriate method.
     * @param rs result set
     * @param ps prepared statement
     * @param conn connection
     * @param scanner scanner
     * @throws SQLException SQL exception
     */
    public void studentEnrollment(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        int selection;

        while(true) {
            printEnrollmentMenu();
            try {
                selection = scanner.nextInt();
                switch (selection) {
                    case 1 -> enrollStudent(ps, conn, scanner);
                    case 2 -> unenrollStudent(ps, conn, scanner);
                    case 3 -> viewStudentEnrollment(rs, ps, conn, scanner);
                    case 4 -> editStudentEnrollment(ps, conn, scanner);
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Please enter an integer between 0 and 4");
                }

            }catch (InputMismatchException ex){
                System.out.println("Please enter an integer value between 0 and 4" );
                scanner.next();
            }
        }
    }

    /**
     * UX will ask for student id and course id and call DB with this query:
     *     Parameter 1: course_id: int
     *     Parameter 2: student_id: int
     * INSERT INTO course_enrollment (course_id, student_id) VALUES (?, ?);
     * @param ps prepared statement
     * @param conn connection
     * @param scanner scanner
     */
    public void enrollStudent(PreparedStatement ps, Connection conn, Scanner scanner) {
        int courseId;
        int studentId;

        try {
            System.out.print("Please enter Course ID: ");
            courseId = scanner.nextInt();

            System.out.print("Please Enter Student ID: ");
            studentId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Number must be an integer");
            return;
        }

        try {
            conn.setAutoCommit(false); // do not autocommit
            ps = conn.prepareStatement(ENROLL_STUDENT);
            ps.setInt(1, courseId);
            ps.setInt(2, studentId);

            int result = ps.executeUpdate();

            if (result > 0) { //success
                System.out.println("Successfully enrolled Student!");
                conn.commit();
            } else {
                System.out.println("Unable to enroll Student");
                conn.rollback();
            }

        } catch (SQLException sqlx) {
            System.out.println("SQL Exception");
        } finally {
            closeResource(ps, conn, scanner);
        }
    }

    /**
     * UX will ask for course enrollment id and call DB with this query:
     *     Parameter 1: course_enrollment_id: int
     * DELETE FROM course_enrollment WHERE course_enrollment_id=?;
     * @param ps prepared statement
     * @param conn connection
     * @param scanner scanner
     */
    public void unenrollStudent(PreparedStatement ps, Connection conn, Scanner scanner) {
        int courseEnrollmentId;

        try {
            System.out.print("Please enter Course Enrollment ID: ");
            courseEnrollmentId = scanner.nextInt();

        } catch (InputMismatchException e) {
            System.out.println("Number must be an integer");
            return;
        }

        try {
            conn.setAutoCommit(false); // do not autocommit
            ps = conn.prepareStatement(UNENROLL_STUDENT);
            ps.setInt(1, courseEnrollmentId);

            int result = ps.executeUpdate();

            if (result > 0) { //success
                System.out.println("Successfully removed Student enrollment!");
                conn.commit();
            } else {
                System.out.println("Unable to remove Student enrollment");
                conn.rollback();
            }

        } catch (SQLException sqlx) {
            System.out.println("SQL Exception");
        } finally {
            closeResource(ps, conn, scanner);
        }
    }

    /**
     * UX will ask for course enrollment id and call DB with this query:
     *     Parameter 1: course_enrollment_id: int
     * SELECT * FROM course_enrollment WHERE course_enrollment_id=?;
     * @param rs result set
     * @param ps prepared statement
     * @param conn connection
     * @param scanner scanner
     */
    public void viewStudentEnrollment(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) {
        int courseEnrollmentId;

        try {
            System.out.print("Please enter Course Enrollment ID: ");
            courseEnrollmentId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Number must be an integer");
            return;
        }

        print(rs, ps, conn, scanner, courseEnrollmentId, VIEW_ENROLLMENT);
    }

    /**
     * UX will ask for course enrollment id, information to update, and call DB with this query:
     *     Parameter 1: course_enrollment_id: int
     *     Parameter 2: Value to change
     *     Parameter 3: New value
     *     Parameter X: …
     * UPDATE course_enrollment SET ?=? WHERE course_enrollment_id=?;
     * @param ps prepared statement
     * @param conn connection
     * @param scanner scanner
     * @throws SQLException SQL exception
     */
    public void editStudentEnrollment(PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        conn.setAutoCommit(false); // do not autocommit
        int courseEnrollmentId;
        int courseId;
        int studentId;

        try {
            System.out.print("Please enter Course Enrollment ID: ");
            courseEnrollmentId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Number must be an integer");
            return;
        }

        System.out.println("Please enter values to change or leave blank to skip");
        System.out.print("Enter an Course ID: ");
        courseId = scanner.nextInt();

        System.out.print("Enter a Student ID: ");
        studentId = scanner.nextInt();

        String statement = "UPDATE course_enrollment SET ";

        boolean c = false;
        boolean s = false;

        if (courseId != 0) {
            statement += "course_id=?,";
            c = true;
        }
        if (studentId != 0) {
            statement += "student_id=?,";
            s = true;
        }

        if (statement.endsWith(",")) {
            statement = statement.substring(0, statement.length()-1);
        }

        statement += " WHERE course_enrollment_id=?";

        if (c || s) {
            try {
                ps = conn.prepareStatement(statement);
                int i = 1;

                if (c) {
                    ps.setInt(i, courseId);
                    i++;
                }
                if (s) {
                    ps.setInt(i, studentId);
                    i++;
                }
                ps.setInt(i, courseEnrollmentId);

                int result = ps.executeUpdate();

                if (result > 0) { //success
                    System.out.println("Updated Course Enrollment!");
                    conn.commit();
                } else {
                    System.out.println("Unable to update Course Enrollment");
                    conn.rollback();
                }
            } catch (SQLException sqlx) {
                System.out.println("SQL Exception");
            } finally {
                closeResource(ps, conn, scanner);
            }
        } else {
            System.out.println("No values to change");
        }
    }

    /**
     * Prints main student menu to console.
     */
    private void printMenu() {
        System.out.println("Student Menu");
        System.out.println("1. Manage Student");
        System.out.println("2. Student Enrollment");
        System.out.println("0. Main Menu");
    }

    /**
     * Prints manage student menu to console.
     */
    private void printManageMenu() {
        System.out.println("Manage Student Sub Menu");
        System.out.println("1. Create Student");
        System.out.println("2. View Students");
        System.out.println("3. Edit Student");
        System.out.println("4. Delete Student");
        System.out.println("5. Student (Report)");
        System.out.println("0. Student Menu");
    }

    /**
     * Prints student enrollment menu to console.
     */
    private void printEnrollmentMenu() {
        System.out.println("Student Enrollment Sub Menu");
        System.out.println("1. Enroll Student");
        System.out.println("2. Unenroll Student");
        System.out.println("3. View Student Enrollment");
        System.out.println("4. Edit Student Enrollment");
        System.out.println("0. Student Menu");
    }

    private void closeResource(PreparedStatement ps, Connection conn, Scanner scanner) {
        try {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (SQLException sqlx) {
            System.out.println("SQL Exception when closing resources");
        }
    }

    private void closeResource(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        try {
            if (conn != null) {
                conn.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (SQLException sqlx) {
            System.out.println("SQL Exception when closing resources");
        }
    }

    private void closeResource(ResultSet rs, Connection conn, Statement stmt) {
        try {
            if (conn != null) {
                conn.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException sqlx) {
            System.out.println("SQL Exception when closing resources");
        }
    }

}
