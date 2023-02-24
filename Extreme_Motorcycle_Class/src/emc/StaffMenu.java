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
import java.util.InputMismatchException;
import java.util.Scanner;

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
        System.out.println("YYYY-MM-DD");
        java.sql.Date date = java.sql.Date.valueOf(scanner.next());

        System.out.println("Enter AM or PM: ");
        String inputAmPm = scanner.next();
        java.sql.Time time_type = java.sql.Time.valueOf(inputAmPm.equalsIgnoreCase("PM") ? LocalTime.of(12, 0) : LocalTime.of(0, 0));

        String query = "SELECT * FROM coach c WHERE c.coach_id NOT IN (SELECT DISTINCT c1.coach_id \n" +
                "FROM coach c1, coach_assignment, course_schedule, time_type\n" +
                "WHERE c1.coach_id = coach_assignment.coach_id\n" +
                "AND coach_assignment.course_schedule_id = course_schedule.course_schedule_id\n" +
                "AND course_schedule.time_type_id = time_type.time_type_id\n" +
                "AND course_schedule.course_date = ? AND time_type.time_type_value= ?);\n";

        try {
            PreparedStatement ps = null;
            ps = conn.prepareStatement(query);
            ps.setDate(1, date);
            ps.setTime(2, time_type);

            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.printSet(rs);
    }

    private void coachWeeklySchedule(ResultSet rs, Statement stmt, Connection conn, Scanner scanner){
        System.out.println("Coach Id");
        int coach_id = scanner.nextInt();

        System.out.println("YYYY-MM-DD");
        java.sql.Date week_start_date = java.sql.Date.valueOf(scanner.next());

        //Calculate new date
        LocalDate newDate = week_start_date.toLocalDate().plusDays(7);
        java.sql.Date week_end_date = java.sql.Date.valueOf(newDate);

        String query = "SELECT DISTINCT course.course_id,course.course_name,course_schedule.course_date,time_type.time_type_value \n" +
                "FROM course,course_schedule,time_type,coach_assignment\n" +
                "WHERE coach_assignment.coach_id=?\n" +
                "AND course.course_id=course_schedule.course_id \n" +
                "AND course_schedule.time_type_id=time_type.time_type_id \n" +
                "AND course_schedule.course_date >= ? \n" +
                "AND course_schedule.course_date < ? \n" +
                "ORDER BY course_schedule.course_date;";


        try {
            PreparedStatement ps = null;
            ps = conn.prepareStatement(query);
            ps.setInt(1, coach_id);
            ps.setDate(2, week_start_date);
            ps.setDate(3, week_end_date);

            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.printSet(rs);
    }

    private void unassignCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void assignCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void deleteCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws  SQLException{
        System.out.println("Enter Coach ID to Delete: ");
        int coach_id = scanner.nextInt();

        String query1 = "DELETE FROM person \n" +
                "WHERE person_id=(\n" +
                "SELECT person_id \n" +
                "FROM coach \n" +
                "WHERE coach_id=?);";
        String query2 = "DELETE FROM coach_assignment WHERE coach_id=?;";
        String query3 = "DELETE FROM coach WHERE coach_id=?;";

        PreparedStatement ps = null;
        conn.setAutoCommit(false);
        ps = conn.prepareStatement(query1);
        ps.setInt(1, coach_id);

        PreparedStatement ps2 = null;
        ps2 = conn.prepareStatement(query2);
        ps2.setInt(1, coach_id);

        PreparedStatement ps3 = null;
        ps3 = conn.prepareStatement(query3);
        ps3.setInt(1, coach_id);

        try {
            if (ps.executeUpdate() > 0 && ps2.executeUpdate() > 0 && ps3.executeUpdate() > 0) {
                conn.commit();
                System.out.println("SUCCESS");
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

    private void editCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void createCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException{
        System.out.println("Name");
        scanner.nextLine();
        String full_name = scanner.nextLine();

        System.out.println("adress");
        String address = scanner.nextLine();

        System.out.println("YYYY-MM-DD");
        String birthDate  = scanner.next();
        java.sql.Date sqlDate = java.sql.Date.valueOf(birthDate);

        System.out.println("Phone");
        String phone = scanner.next();

        System.out.println("classroom_certified");
        int classroom_certified = scanner.nextInt();

        System.out.println("dirtbike_certified");
        int dirtbike_certified = scanner.nextInt();

        System.out.println("streetbike_certified");
        int streetbike_certified = scanner.nextInt();

        String query1 = "INSERT INTO person (full_name, address, date_birth, phone) VALUES (?, ?, ?, ?);";
        String query2 = "INSERT INTO coach (person_id, classroom_certified, dirtbike_certified, streetbike_certified)\n" +
                "VALUES ((SELECT person_id FROM person WHERE full_name=? AND address=? AND date_birth=? AND phone= ?), ?, ?, ?);";
        PreparedStatement ps = null;
        conn.setAutoCommit(false);
        ps = conn.prepareStatement(query1);
        ps.setString(1, full_name);
        ps.setString(2, address);
        ps.setDate(3, sqlDate);
        ps.setString(4, phone);

        PreparedStatement ps1 = null;
        ps1 = conn.prepareStatement(query2);
        ps1.setString(1, full_name);
        ps1.setString(2, address);
        ps1.setDate(3, sqlDate);
        ps1.setString(4, phone);
        ps1.setInt(5, classroom_certified);
        ps1.setInt(6, dirtbike_certified);
        ps1.setInt(7, streetbike_certified);
        try {
            if (ps.executeUpdate() > 0 && ps1.executeUpdate() > 0) {
                conn.commit();
                System.out.println("SUCCESS");
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

}
