package emc;

/**
 * StudentMenu.java
 *
 * Prints and handles Student sub menu.
 *
 * Group 4
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class StudentMenu {

    /**
     * Print student menu options and call appropriate method.
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void menu(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        int selection;

        while(true) {
            printMenu();
            try {
                selection = scanner.nextInt();
                switch (selection){
                    case 1: manageStudent(rs, stmt, conn, scanner); break;
                    case 2: studentEnrollment(rs, stmt, conn, scanner); break;
                    case 0: return;
                    default:
                        System.out.println("Please enter an integer between 0 and 2");
                }

            }catch (InputMismatchException ex){
                System.out.println("Please enter an integer value between 0 and 2" );
                scanner.next();
            }
        }
    }

    /**
     * Print manage student sub menu options and call appropriate method.
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void manageStudents(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        int selection;

        while(true) {
            printManageMenu();
            try {
                selection = scanner.nextInt();
                switch (selection){
                    case 1: createStudent(rs, stmt, conn, scanner); break;
                    case 2: viewStudents(rs, stmt, conn); break;
                    case 3: editStudent(rs, stmt, conn, scanner); break;
                    case 4: deleteStudent(rs, stmt, conn, scanner); break;
                    case 5: studentReport(rs, stmt, conn, scanner); break;
                    case 0: return;
                    default:
                        System.out.println("Please enter an integer between 0 and 5");
                }

            }catch (InputMismatchException ex){
                System.out.println("Please enter an integer value between 0 and 5" );
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
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void createStudent(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        conn.setAutoCommit(false); // do not autocommit

        System.out.println("Please enter a Student name between 1-30 characters: ");
        String student_name = scanner.next();

        System.out.println("Please enter an address between 1-50 characters: ");
        String address = scanner.next();

        try {
            System.out.println("Please enter a numeric birth month value: ");
            int birth_month = scanner.nextInt();

            System.out.println("Please enter birth day: ");
            int birth_day = scanner.nextInt();

            System.out.println("Please enter birth year: ");
            int birth_year = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Number must be an integer");
            return;
        }

        if (birth_month > 0 && birth_month < 13 && birth_day > 0 && birth_day < 32) {
            Date date_birth = new Date(birth_year, birth_month, birth_day);
        } else {
            System.out.println("Birthday values out of range");
            return;
        }


        System.out.println("Please enter phone number (5555551234): ");
        String phone = scanner.next();

        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO person (full_name, address, date_birth, phone) " +
                            "VALUES (?, ?, ?, ?)");
            stmt.setString(1, student_name);
            stmt.setString(2, address);
            stmt.setDate(3, date_birth);
            stmt.setString(4, phone);

            int result = stmt.executeUpdate();

            if (result > 0) { //success
                stmt = conn.prepareStatement(
                        "SELECT person_id FROM person " +
                                "WHERE full_name=? AND address=? " +
                                "AND date_birth=? AND phone=?");
                stmt.setString(1, student_name);
                stmt.setString(2, address);
                stmt.setDate(3, date_birth);
                stmt.setString(4, phone);

                rs = stmt.executeQuery();
                int person_id;

                while (rs.next()) {
                    person_id = rs.getInt(1);
                }

                if (person_id != null) { //success
                    stmt = conn.prepareStatement("INSERT INTO student (person_id) VALUES (?)");
                    stmt.setString(1, person_id);
                    int result = stmt.executeUpdate();

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
                }
            } else {
                System.out.println("Unable to create Person");
                conn.rollback();
            }
        } catch (SQLException sqlx) {
            System.out.println("SQL Exception");
        } finally {
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

    /**
     * UX will call DB with this query:
     * SELECT student.student_id,person.full_name
     * FROM student,person
     * WHERE student.person_id=person.person_id;
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void viewStudents(ResultSet rs, Statement stmt, Connection conn) {
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                    "SELECT student.student_id,person.full_name " +
                            "FROM student,person " +
                            "WHERE student.person_id=person.person_id");

            System.out.printf("|%20s|%20s|\n","Student ID", "Name");
            System.out.println("________________________________________");
            while (rs.next()) {
                System.out.printf("|%20s|%20s|\n",rs.getInt(1), rs.getString(2));
            }
        } catch (SQLException sqlx) {
            System.out.println("SQL Exception.");
        } finally {
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
                System.out.println("SQL Exception when closing resources.");
            }
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
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void editStudent(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        conn.setAutoCommit(false); // do not autocommit

        try {
            System.out.println("Please enter Student ID: ");
            int student_id = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Number must be an integer");
            return;
        }

        System.out.println("Please enter values to change or leave blank to skip.");
        System.out.println("Enter a Student name between 1-30 characters: ");
        String student_name = scanner.next();

        System.out.println("Enter an address between 1-50 characters: ");
        String address = scanner.next();

        try {
            System.out.println("Month, day, and year must be entered to change birthday.");
            System.out.println("Enter a numeric birth month value: ");
            int birth_month = scanner.nextInt();

            System.out.println("Enter birth day: ");
            int birth_day = scanner.nextInt();

            System.out.println("Enter birth year: ");
            int birth_year = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Number must be an integer");
            return;
        }

        if (birth_month > 0 && birth_month < 13 && birth_day > 0 and birth_day < 32) {
            Date date_birth = new Date(birth_year, birth_month, birth_day);
        } else {
            System.out.println("Birthday values out of range");
            date=birth_day = null;
        }

        System.out.println("Please enter phone number (5555551234): ");
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
        if (address != null ** !address.equals("")) {
            statement += "address=?,";
            addy = true;
        }
        if (date_birth != null) {
            statement += "date_birth=?";
            bday = true;
        }
        if (phone != null && !phone.equals("")) {
            statement += "phone=?";
            tele = true;
        }

        if (statement.endsWith(",")) {
            statement = statement.substring(0, statement.length()-1);
        }

        statement += " FROM person,student WHERE person.person_id=student.person_id AND student.student_id=?";

        if (name || addy || bday || tele) {
            try {
                stmt = conn.prepareStatement(statement);
                int i = 1;

                if (name) {
                    stmt.setString(i, student_name);
                    i++;
                }
                if (addy) {
                    stmt.setString(i, address);
                    i++;
                }
                if (bday) {
                    stmt.setString(i, date_birth);
                    i++;
                }
                if (tele) {
                    stmt.setString(i, phone);
                    i++;
                }
                stmt.setString(i, student_id);

                int result = stmt.executeUpdate();

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
        } else {
            System.out.println("No values to change");
            return;
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
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void deleteStudent(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {

    }

    /**
     * UX will ask for student id and call DB with this query:
     *     Parameter 1: student_id:  int
     * SELECT course_enrollment.course_id, course.course_name, course_enrollment.paid
     * FROM course_enrollment,course
     * WHERE course_enrollment.course_id=course.course_id
     * AND course_enrollment.student_id=?;
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void studentReport(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {

    }

    /**
     * Print student enrollment sub menu options and call appropriate method.
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void studentEnrollment(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        int selection;

        while(true) {
            printEnrollmentMenu();
            try {
                selection = scanner.nextInt();
                switch (selection){
                    case 1: enrollStudent(rs, stmt, conn, scanner); break;
                    case 2: unenrollStudent(rs, stmt, conn, scanner); break;
                    case 3: viewStudentEnrollment(rs, stmt, conn, scanner); break;
                    case 4: editStudentEnrollment(rs, stmt, conn, scanner); break;
                    case 0: return;
                    default:
                        System.out.println("Please enter an integer between 0 and 4");
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
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void enrollStudent(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {

    }

    /**
     * UX will ask for course enrollment id and call DB with this query:
     *     Parameter 1: course_enrollment_id: int
     * DELETE FROM course_enrollment WHERE course_enrollment_id=?;
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void unenrollStudent(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {

    }

    /**
     * UX will ask for course enrollment id and call DB with this query:
     *     Parameter 1: course_enrollment_id: int
     * SELECT * FROM course_enrollment WHERE course_enrollment_id=?;
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void viewStudentEnrollment(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {

    }

    /**
     * UX will ask for course enrollment id, information to update, and call DB with this query:
     *     Parameter 1: course_enrollment_id: int
     *     Parameter 2: Value to change
     *     Parameter 3: New value
     *     Parameter X: …
     * UPDATE course_enrollment SET ?=? WHERE course_enrollment_id=?;
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void editStudentEnrollment(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {

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

}
