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
import java.util.InputMismatchException;
import java.util.Scanner;

public class StaffMenu {

    public void menu(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws  SQLException {
        int choice;

        while(true) {
            printStaffMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> createCoach(ps, conn, scanner);
                    case 2 -> viewCoach(rs, ps, conn);
                    case 3 -> editCoach(ps, conn, scanner);
                    case 4 -> deleteCoach(ps, conn, scanner);
                    case 5 -> assignCoach(ps, conn, scanner);
                    case 6 -> unassignCoach(ps, conn, scanner);
                    case 7 -> viewCoachSchedule(rs, ps, conn, scanner);
                    case 0 -> {
                        return;
                    }
                }
            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Please enter an integer value between 0 and 7");
                scanner.nextLine();
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

    private void viewCoach(ResultSet rs, PreparedStatement ps, Connection conn) {
        System.out.println(": ");
        String query = """
                SELECT coach_id, classroom_certified, dirtbike_certified, streetbike_certified, full_name, address, date_birth, phone
                FROM coach,person\s
                WHERE coach.person_id=person.person_id;
                """; 
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.printSet(rs);
    }

    private void viewCoachSchedule(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) {
        int choice;

        while(true) {
            printSchedulesMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> coachWeeklySchedule(rs, ps, conn, scanner);
                    case 2 -> coachAvailabilitySchedule(rs, ps, conn, scanner);
                    case 0 -> {
                        return;
                    }
                }
            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Please enter an integer value between 0 and 2");
                scanner.nextLine();
            }
        }
    }

    private void coachAvailabilitySchedule(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) {
        System.out.println("YYYY-MM-DD");
        String date = scanner.nextLine();

        System.out.println("Enter 'AM range' or 'PM range': ");
        String time_type = scanner.nextLine();

        String query = """
                SELECT * FROM coach c WHERE c.coach_id NOT IN (SELECT DISTINCT c1.coach_id\s
                FROM coach c1, coach_assignment, course_schedule, time_type
                WHERE c1.coach_id = coach_assignment.coach_id
                AND coach_assignment.course_schedule_id = course_schedule.course_schedule_id
                AND course_schedule.time_type_id = time_type.time_type_id
                AND course_schedule.course_date = ? AND time_type.time_type_value= ?);
                """;

        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, date);
            ps.setString(2, time_type);
            System.out.println(ps);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.printSet(rs);
    }

    private void coachWeeklySchedule(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner){
        System.out.println("Coach Id");
        int coach_id = Integer.parseInt(scanner.nextLine());

        System.out.println("YYYY-MM-DD");
        java.sql.Date week_start_date = java.sql.Date.valueOf(scanner.nextLine());

        //Calculate new date
        LocalDate newDate = week_start_date.toLocalDate().plusDays(7);
        java.sql.Date week_end_date = java.sql.Date.valueOf(newDate);

        String query = """
                SELECT DISTINCT course.course_id,course.course_name,course_schedule.course_date,time_type.time_type_value\s
                FROM course,course_schedule,time_type,coach_assignment
                WHERE coach_assignment.coach_id=?
                AND coach_assignment.course_schedule_id = course_schedule.course_schedule_id
                AND course.course_id=course_schedule.course_id\s
                AND course_schedule.time_type_id=time_type.time_type_id\s
                AND course_schedule.course_date >= ?\s
                AND course_schedule.course_date < ?\s
                ORDER BY course_schedule.course_date;""";


        try {
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

    private void unassignCoach(PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter Coach ID");
        int coach_id = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter Schedule ID: ");
        int course_schedule_id = Integer.parseInt(scanner.nextLine());

        String query1 = """
                DELETE FROM coach_assignment\s
                WHERE coach_id=?\s
                AND course_schedule_id=?;
                """;

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
            e.printStackTrace();
        }
    }

    private void assignCoach(PreparedStatement ps, Connection conn, Scanner scanner) throws  SQLException{
        System.out.println("Enter Schedule ID: ");
        int course_schedule_id = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter Coach ID");
        int coach_id = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter Assigned Role: ");
        String assigned_role = scanner.nextLine();

        String query1 = "INSERT INTO coach_assignment (course_schedule_id, coach_id, assigned_role) VALUES (?, ?, ?);";
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

    private void deleteCoach(PreparedStatement ps, Connection conn, Scanner scanner) throws  SQLException{
        System.out.println("Enter Coach ID to Delete: ");
        int coach_id = Integer.parseInt(scanner.nextLine());

        String query1 = """
                DELETE FROM person\s
                WHERE person_id=(
                SELECT person_id\s
                FROM coach\s
                WHERE coach_id=?);""";
        String query2 = "DELETE FROM coach_assignment WHERE coach_id=?;";
        String query3 = "DELETE FROM coach WHERE coach_id=?;";

        conn.setAutoCommit(false);
        ps = conn.prepareStatement(query1);
        ps.setInt(1, coach_id);

        PreparedStatement ps2;
        ps2 = conn.prepareStatement(query2);
        ps2.setInt(1, coach_id);

        PreparedStatement ps3;
        ps3 = conn.prepareStatement(query3);
        ps3.setInt(1, coach_id);

        
        ps.executeUpdate();
        try {
            conn.commit();
            System.out.println("SUCCESS");
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

    private void editCoach(PreparedStatement ps, Connection conn, Scanner scanner) {
        int choice;

        while(true) {
            printEditMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> editAddress(ps, conn, scanner);
                    case 2 -> editDateBirth(ps, conn, scanner);
                    case 3 -> editPhone(ps, conn, scanner);
                    case 4 -> editClassroom(ps, conn, scanner);
                    case 5 -> editDirtBike(ps, conn, scanner);
                    case 6 -> editStreetBike(ps, conn, scanner);
                    case 0 -> {
                        return;
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter an integer value between 0 and 6");
                scanner.nextLine();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }

    }

    private void editAddress(PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Coach Id");
        int coach_id = Integer.parseInt(scanner.nextLine());

        System.out.println("Address");
        String address = scanner.nextLine();

        conn.setAutoCommit(false);

        String query = """
                UPDATE person
                SET person.address=?
                WHERE person.person_id=(
                SELECT DISTINCT person_id\s
                FROM coach\s
                WHERE coach_id=?);
                """;
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

    private void editDateBirth(PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Coach Id");
        int coach_id = Integer.parseInt(scanner.nextLine());

        System.out.println("YYYY-MM-DD");
        java.sql.Date date_birth = java.sql.Date.valueOf(scanner.nextLine());

        conn.setAutoCommit(false);

        String query = """
                UPDATE person
                SET person.date_birth=?
                WHERE person.person_id=(
                SELECT DISTINCT person_id\s
                FROM coach\s
                WHERE coach_id=?);
                """;
        ps = conn.prepareStatement(query);
        ps.setDate(1, date_birth);
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

    private void editPhone(PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Coach Id");
        int coach_id = Integer.parseInt(scanner.nextLine());

        System.out.println("Phone");
        scanner.nextLine();
        String phone = scanner.nextLine();

        conn.setAutoCommit(false);

        String query = """
                UPDATE person
                SET person.phone=?
                WHERE person.person_id=(
                SELECT DISTINCT person_id\s
                FROM coach\s
                WHERE coach_id=?);
                """;
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

    private void editClassroom(PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Coach Id");
        int coach_id = Integer.parseInt(scanner.nextLine());

        System.out.println("Classroom Certified");
        int classroom_certified = Integer.parseInt(scanner.nextLine());

        conn.setAutoCommit(false);

        String query = """
                UPDATE coach
                SET coach.classroom_certified=?
                WHERE coach_id=?;
                """;
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

    private void editDirtBike(PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Coach Id");
        int coach_id = Integer.parseInt(scanner.nextLine());

        System.out.println("Dirt Bike Certified");
        int dirtbike_certified = Integer.parseInt(scanner.nextLine());

        conn.setAutoCommit(false);

        String query = """
                UPDATE coach
                SET coach.dirtbike_certified=?
                WHERE coach_id=?;
                """;
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

    private void editStreetBike(PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Coach Id");
        int coach_id = Integer.parseInt(scanner.nextLine());

        System.out.println("Street Bike Certified");
        int streetbike_certified = Integer.parseInt(scanner.nextLine());

        conn.setAutoCommit(false);

        String query = """
                UPDATE coach
                SET coach.streetbike_certified=?
                WHERE coach_id=?;
                """;
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

    private void createCoach(PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException{
        System.out.println("Name");
        String full_name = scanner.nextLine();

        System.out.println("address");
        String address = scanner.nextLine();

        System.out.println("YYYY-MM-DD");
        String birthDate  = scanner.nextLine();
        java.sql.Date sqlDate = java.sql.Date.valueOf(birthDate);

        System.out.println("Phone");
        String phone = scanner.nextLine();

        System.out.println("classroom_certified");
        int classroom_certified = Integer.parseInt(scanner.nextLine());

        System.out.println("dirtbike_certified");
        int dirtbike_certified = Integer.parseInt(scanner.nextLine());

        System.out.println("streetbike_certified");
        int streetbike_certified = Integer.parseInt(scanner.nextLine());

        String query1 = "INSERT INTO person (full_name, address, date_birth, phone) VALUES (?, ?, ?, ?);";
        String query2 = "INSERT INTO coach (person_id, classroom_certified, dirtbike_certified, streetbike_certified)\n" +
                "VALUES ((SELECT person_id FROM person WHERE full_name=? AND address=? AND date_birth=? AND phone= ?), ?, ?, ?);";
        
        conn.setAutoCommit(false);
        ps = conn.prepareStatement(query1);
        ps.setString(1, full_name);
        ps.setString(2, address);
        ps.setDate(3, sqlDate);
        ps.setString(4, phone);

        PreparedStatement ps1;
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
