package emc;

/**
 * StaffMenu.java
 *
 * Prints and handles Staff sub menu.
 *
 * Group 4
 */

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class StaffMenu {

    public void menu(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws  SQLException {
        int choice;

        while(true) {
            printStaffMenu();
            try {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        createCoach(rs, stmt, conn, scanner);
                        break;
                    case 2:
                        viewCoach(rs, stmt, conn, scanner);
                        break;
                    case 3:
                        editCoach(rs, stmt, conn, scanner);
                        break;
                    case 4:
                        deleteCoach(rs, stmt, conn, scanner);
                        break;
                    case 5:
                        assignCoach(rs, stmt, conn, scanner);
                        break;
                    case 6:
                        unassignCoach(rs, stmt, conn, scanner);
                        break;
                    case 7:
                        viewCoachSchedule(rs, stmt, conn, scanner);
                        break;
                    case 0:
                        return;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter an integer value between 0 and 7");
                scanner.next();
            }
        }

    }
    
        private void printStaffMenu() {
        System.out.println("Coach Menu");
        System.out.println("1. Create Coach");
        System.out.println("2. View Coach");
        System.out.println("3. Edit Coach");
        System.out.println("4. Delete Coach");
        System.out.println("5. Assign Coach");
        System.out.println("6. Unassign Coach");
        System.out.println("7. View Coach Schedule");
        System.out.println("0. Main Menu");
    }

    private void printSchedulesMenu() {
        System.out.println("View Coach Schedule Sub Menu");
        System.out.println("1. Specific Coach Schedule");
        System.out.println("2. Availability Schedule");
        System.out.println("0. Coach Menu");
    }

    private void printEditMenu() {
        System.out.println("Choose What to Edit: ");
        System.out.println("1. Address");
        System.out.println("2. Birth Date");
        System.out.println("3. Phone Number");
        System.out.println("4. Classroom Certified");
        System.out.println("5. Dirt Bike Certified");
        System.out.println("6. Street Bike Certified");
        System.out.println("0. Coach Menu");
    }

    private void viewCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println(": ");
        String query = "SELECT coach.coach_id,person.full_name\n" +
                "FROM coach,person \n" +
                "WHERE coach.person_id=person.person_id;\n";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.printSet(rs);
    }

    private void viewCoachSchedule(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        int choice;

        while(true) {
            printSchedulesMenu();
            try {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        coachWeeklySchedule(rs, stmt, conn, scanner);
                        break;
                    case 2:
                        coachAvailabilitySchedule(rs, stmt, conn, scanner);
                        break;
                    case 0:
                        return;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter an integer value between 0 and 2");
                scanner.next();
            }
        }
    }

    private void coachAvailabilitySchedule(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter the date (format: yyyy-mm-dd): ");
        scanner.nextLine();
        String date = scanner.nextLine().trim();
        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format. Please enter date in YYYY-MM-DD format.");
            return;
        }

        System.out.println("Please enter the time type ('AM range' or 'PM range'): ");
        String time_type = scanner.nextLine();
        Pattern pattern = Pattern.compile("^(AM|PM) range$");

        if (pattern.matcher(time_type).matches()) {
            // valid input format, proceed with processing
            LocalTime localTime = LocalTime.parse("12:00:00"); // replace with your own parsing logic
            // rest of your code
        } else {
            // invalid input format, display an error message or handle the error
            System.out.println("Error: Invalid time_type format. Please enter 'AM range' or 'PM range'.");
            return;
        }

        String query = "SELECT * FROM coach c WHERE c.coach_id NOT IN (SELECT DISTINCT c1.coach_id \n" +
                "FROM coach c1, coach_assignment, course_schedule, time_type\n" +
                "WHERE c1.coach_id = coach_assignment.coach_id\n" +
                "AND coach_assignment.course_schedule_id = course_schedule.course_schedule_id\n" +
                "AND course_schedule.time_type_id = time_type.time_type_id\n" +
                "AND course_schedule.course_date = ? AND time_type.time_type_value= ?);\n";

        try {
            PreparedStatement ps = null;
            ps = conn.prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.setString(2, time_type);
//            System.out.println(ps.toString());
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.printSet(rs);
    }

    private void coachWeeklySchedule(ResultSet rs, Statement stmt, Connection conn, Scanner scanner){
        System.out.println("Please enter the coach ID: ");
        int coach_id = scanner.nextInt();

        System.out.println("Please enter the start date of the week (format: yyyy-mm-dd): ");
        scanner.nextLine();
        String week_start_date = scanner.nextLine().trim();
        try {
            LocalDate.parse(week_start_date);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format. Please enter date in YYYY-MM-DD format.");
            return;
        }

        //Calculate new date
        LocalDate newDate = LocalDate.parse(week_start_date).plusDays(7);
        java.sql.Date week_end_date = java.sql.Date.valueOf(newDate);

        String query = "SELECT DISTINCT course.course_id,course.course_name,course_schedule.course_date,time_type.time_type_value \n" +
                "FROM course,course_schedule,time_type,coach_assignment\n" +
                "WHERE coach_assignment.coach_id=?\n" +
                "AND coach_assignment.course_schedule_id = course_schedule.course_schedule_id\n" +
                "AND course.course_id=course_schedule.course_id \n" +
                "AND course_schedule.time_type_id=time_type.time_type_id \n" +
                "AND course_schedule.course_date >= ? \n" +
                "AND course_schedule.course_date < ? \n" +
                "ORDER BY course_schedule.course_date;";


        try {
            PreparedStatement ps = null;
            ps = conn.prepareStatement(query);
            ps.setInt(1, coach_id);
            ps.setDate(2, java.sql.Date.valueOf(week_start_date));
            ps.setDate(3, week_end_date);

            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.printSet(rs);
    }

    private void unassignCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter Coach ID");
        int coach_id = scanner.nextInt();

        System.out.println("Enter Schedule ID: ");
        int course_schedule_id = scanner.nextInt();

        String query1 = "DELETE FROM coach_assignment \n" +
                "WHERE coach_id=? \n" +
                "AND course_schedule_id=?;\n";

        PreparedStatement ps = null;
        conn.setAutoCommit(false);
        ps = conn.prepareStatement(query1);
        ps.setInt(1, coach_id);
        ps.setInt(2, course_schedule_id);

        try {
            if (ps.executeUpdate() > 0) {
                conn.commit();
                System.out.println("SUCCESS");
            }
        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Error unassigning coach: " + e.getMessage());
        }
    }

    private void assignCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws  SQLException{
        System.out.println("Please enter the course schedule ID: ");
        int course_schedule_id = scanner.nextInt();

        System.out.println("Please enter the coach ID: ");
        int coach_id = scanner.nextInt();

        System.out.println("Please enter the assigned role ('Range' or 'Classroom'): ");
        String assigned_role = scanner.next();
        if (!assigned_role.matches("Range|Classroom")) {
            System.out.println("Error: Invalid input. Please enter 'Range' or 'Classroom'.");
            return;
        }


        String query1 = "INSERT INTO coach_assignment (course_schedule_id, coach_id, assigned_role) VALUES (?, ?, ?);";
        PreparedStatement ps = null;
        conn.setAutoCommit(false);
        ps = conn.prepareStatement(query1);
        ps.setInt(1, course_schedule_id);
        ps.setInt(2, coach_id);
        ps.setString(3, assigned_role);

        try {
            if (ps.executeUpdate() > 0) {
                conn.commit();
                System.out.println("SUCCESS");
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

    private void deleteCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws  SQLException{
        System.out.println("Enter Coach ID to Delete: ");
        int coach_id = scanner.nextInt();

        String query1 = "DELETE FROM person \n" +
                "WHERE person_id=(\n" +
                "SELECT person_id \n" +
                "FROM coach \n" +
                "WHERE coach_id=?);";


        PreparedStatement ps = null;
        conn.setAutoCommit(false);
        ps = conn.prepareStatement(query1);
        ps.setInt(1, coach_id);

        try {
            if (ps.executeUpdate() > 0) {
                conn.commit();
                System.out.println("SUCCESS");
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

    private void editCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        int choice;

        while(true) {
            printEditMenu();
            try {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        editAddress(rs, stmt, conn, scanner);
                        break;
                    case 2:
                        editDateBirth(rs, stmt, conn, scanner);
                        break;
                    case 3:
                        editPhone(rs, stmt, conn, scanner);
                        break;
                    case 4:
                        editClassroom(rs, stmt, conn, scanner);
                        break;
                    case 5:
                        editDirtBike(rs, stmt, conn, scanner);
                        break;
                    case 6:
                        editStreetBike(rs, stmt, conn, scanner);
                        break;
                    case 0:
                        return;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter an integer value between 0 and 6");
                scanner.next();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }

    }

    private void editAddress(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Please enter the coach ID you want to edit: ");
        int coach_id = scanner.nextInt();

        System.out.println("Please enter the new address (max 50 characters): ");
        scanner.nextLine();
        String address = scanner.nextLine();
        if (address.length() > 50) {
            System.out.println("Error: Address exceeds maximum length of 50 characters.");
            return;
        }

        conn.setAutoCommit(false);

        PreparedStatement ps = null;
        String query = "UPDATE person\n" +
                "SET person.address=?\n" +
                "WHERE person.person_id=(\n" +
                "SELECT DISTINCT person_id \n" +
                "FROM coach \n" +
                "WHERE coach_id=?);\n";
        ps = conn.prepareStatement(query);
        ps.setString(1, address);
        ps.setInt(2, coach_id);
        try
        {
            if (ps.executeUpdate() > 0)
            {
                conn.commit();
                System.out.println("SUCCESS");
            }
        }
        catch (SQLException ex)
        {
            conn.rollback();
            System.out.println("ERROR: Problem with Update." + ex.getMessage());
        }
    }

    private void editDateBirth(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Please enter the coach ID you want to edit: ");
        int coach_id = scanner.nextInt();

        System.out.println("Please enter the new date (format: yyyy-mm-dd): ");
        scanner.nextLine();
        String date_birth = scanner.nextLine().trim();
        try {
            LocalDate.parse(date_birth);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format. Please enter date in YYYY-MM-DD format.");
            return;
        }

        conn.setAutoCommit(false);

        PreparedStatement ps = null;
        String query = "UPDATE person\n" +
                "SET person.date_birth=?\n" +
                "WHERE person.person_id=(\n" +
                "SELECT DISTINCT person_id \n" +
                "FROM coach \n" +
                "WHERE coach_id=?);\n";
        ps = conn.prepareStatement(query);
        ps.setDate(1, java.sql.Date.valueOf(date_birth));
        ps.setInt(2, coach_id);
        try
        {
            if (ps.executeUpdate() > 0)
            {
                conn.commit();
                System.out.println("SUCCESS");
            }
        }
        catch (SQLException ex)
        {
            conn.rollback();
            System.out.println("ERROR: Problem with Update." + ex.getMessage());
        }
    }

    private void editPhone(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Please enter the coach ID you want to edit: ");
        int coach_id = scanner.nextInt();

        System.out.println("Please enter the new phone number: ");
        scanner.nextLine();
        String phone = scanner.nextLine();
        if (!phone.matches("\\d{10}")) {
            System.out.println("Error: Invalid phone number. Please enter a 10-digit number.");
            return;
        }

        conn.setAutoCommit(false);

        PreparedStatement ps = null;
        String query = "UPDATE person\n" +
                "SET person.phone=?\n" +
                "WHERE person.person_id=(\n" +
                "SELECT DISTINCT person_id \n" +
                "FROM coach \n" +
                "WHERE coach_id=?);\n";
        ps = conn.prepareStatement(query);
        ps.setString(1, phone);
        ps.setInt(2, coach_id);
        try
        {
            if (ps.executeUpdate() > 0)
            {
                conn.commit();
                System.out.println("SUCCESS");
            }
        }
        catch (SQLException ex)
        {
            conn.rollback();
            System.out.println("ERROR: Problem with Update." + ex.getMessage());
        }
    }

    private void editClassroom(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Please enter the coach ID you want to edit: ");
        int coach_id = scanner.nextInt();

        System.out.println("Please enter whether the coach is Classroom Certified or not (0 or 1): ");
        int classroom_certified = scanner.nextInt();
        if (classroom_certified != 0 && classroom_certified != 1) {
            System.out.println("Error: Invalid input. Please enter either 0 or 1.");
            return;
        }

        conn.setAutoCommit(false);

        PreparedStatement ps = null;
        String query = "UPDATE coach\n" +
                "SET coach.classroom_certified=?\n" +
                "WHERE coach_id=?;\n";
        ps = conn.prepareStatement(query);
        ps.setInt(1, classroom_certified);
        ps.setInt(2, coach_id);
        try
        {
            if (ps.executeUpdate() > 0)
            {
                conn.commit();
                System.out.println("SUCCESS");
            }
        }
        catch (SQLException ex)
        {
            conn.rollback();
            System.out.println("ERROR: Problem with Update." + ex.getMessage());
        }
    }

    private void editDirtBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Please enter the coach ID you want to edit: ");
        int coach_id = scanner.nextInt();

        System.out.println("Please enter whether the coach is Dirt Bike Certified or not (0 or 1): ");
        int dirtbike_certified = scanner.nextInt();
        if (dirtbike_certified != 0 && dirtbike_certified != 1) {
            System.out.println("Error: Invalid input. Please enter either 0 or 1.");
            return;
        }

        conn.setAutoCommit(false);

        PreparedStatement ps = null;
        String query = "UPDATE coach\n" +
                "SET coach.dirtbike_certified=?\n" +
                "WHERE coach_id=?;\n";
        ps = conn.prepareStatement(query);
        ps.setInt(1, dirtbike_certified);
        ps.setInt(2, coach_id);
        try
        {
            if (ps.executeUpdate() > 0)
            {
                conn.commit();
                System.out.println("SUCCESS");
            }
        }
        catch (SQLException ex)
        {
            conn.rollback();
            System.out.println("ERROR: Problem with Update." + ex.getMessage());
        }
    }

    private void editStreetBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Please enter the coach ID you want to edit: ");
        int coach_id = scanner.nextInt();

        System.out.println("Please ender whether the coach is Street Bike Certified or not (0 or 1):");
        int streetbike_certified = scanner.nextInt();
        if (streetbike_certified != 0 && streetbike_certified != 1) {
            System.out.println("Error: Invalid input. Please enter either 0 or 1.");
            return;
        }
        conn.setAutoCommit(false);

        PreparedStatement ps = null;
        String query = "UPDATE coach\n" +
                "SET coach.streetbike_certified=?\n" +
                "WHERE coach_id=?;\n";
        ps = conn.prepareStatement(query);
        ps.setInt(1, streetbike_certified);
        ps.setInt(2, coach_id);
        try
        {
            if (ps.executeUpdate() > 0)
            {
                conn.commit();
                System.out.println("SUCCESS");
            }
        }
        catch (SQLException ex)
        {
            conn.rollback();
            System.out.println("ERROR: Problem with Update." + ex.getMessage());
        }
    }

    private void createCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException{
        System.out.println("Please enter the coach's full name (max 30 characters): ");
        scanner.nextLine();
        String full_name = scanner.nextLine().trim();
        if (full_name.length() > 30) {
            System.out.println("Error: Full name exceeds maximum length of 30 characters.");
            return;
        }

        System.out.println("Please enter the coach's address (max 50 characters): ");
        String address = scanner.nextLine().trim();
        if (address.length() > 50) {
            System.out.println("Error: Address exceeds maximum length of 50 characters.");
            return;
        }

        System.out.println("Please enter the coach's date of birth (YYYY-MM-DD): ");
        String date_birth = scanner.nextLine().trim();
        try {
            LocalDate.parse(date_birth);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format. Please enter date in YYYY-MM-DD format.");
            return;
        }

        System.out.println("Please enter the coach's phone number (10 digits): ");
        String phone = scanner.nextLine().trim();
        if (!phone.matches("\\d{10}")) {
            System.out.println("Error: Invalid phone number. Please enter a 10-digit number.");
            return;
        }

        System.out.println("Is the coach certified to teach in the classroom? (0 for no, 1 for yes)");
        int classroom_certified = scanner.nextInt();
        if (classroom_certified != 0 && classroom_certified != 1) {
            System.out.println("Error: Invalid input. Please enter either 0 or 1.");
            return;
        }

        System.out.println("Is the coach certified to teach dirtbike riding? (0 for no, 1 for yes)");
        int dirtbike_certified = scanner.nextInt();
        if (dirtbike_certified != 0 && dirtbike_certified != 1) {
            System.out.println("Error: Invalid input. Please enter either 0 or 1.");
            return;
        }

        System.out.println("Is the coach certified to teach streetbike riding? (0 for no, 1 for yes)");
        int streetbike_certified = scanner.nextInt();
        if (streetbike_certified != 0 && streetbike_certified != 1) {
            System.out.println("Error: Invalid input. Please enter either 0 or 1.");
            return;
        }

        String query1 = "INSERT INTO person (full_name, address, date_birth, phone) VALUES (?, ?, ?, ?);";
        String query2 = "INSERT INTO coach (person_id, classroom_certified, dirtbike_certified, streetbike_certified)\n" +
                "VALUES ((SELECT person_id FROM person WHERE full_name=? AND address=? AND date_birth=? AND phone= ?), ?, ?, ?);";
        PreparedStatement ps = null;
        conn.setAutoCommit(false);
        ps = conn.prepareStatement(query1);
        ps.setString(1, full_name);
        ps.setString(2, address);
        ps.setDate(3, java.sql.Date.valueOf(date_birth));
        ps.setString(4, phone);

        PreparedStatement ps1 = null;
        ps1 = conn.prepareStatement(query2);
        ps1.setString(1, full_name);
        ps1.setString(2, address);
        ps1.setDate(3, java.sql.Date.valueOf(date_birth));
        ps1.setString(4, phone);
        ps1.setInt(5, classroom_certified);
        ps1.setInt(6, dirtbike_certified);
        ps1.setInt(7, streetbike_certified);
        try {
            if (ps.executeUpdate() > 0 && ps1.executeUpdate() > 0) {
                conn.commit();
                System.out.println("Coach " + full_name + " has been added to the database.");
            }
        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Error creating coach: " + e.getMessage());
        }
    }

}
