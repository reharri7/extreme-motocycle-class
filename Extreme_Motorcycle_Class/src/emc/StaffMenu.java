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

    public void menu(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws  SQLException {
        int choice;

        while(true) {
            printStaffMenu();
            try {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        createCoach(rs, ps, conn, scanner);
                        break;
                    case 2:
                        viewCoach(rs, ps, conn, scanner);
                        break;
                    case 3:
                        editCoach(rs, ps, conn, scanner);
                        break;
                    case 4:
                        deleteCoach(rs, ps, conn, scanner);
                        break;
                    case 5:
                        assignCoach(rs, ps, conn, scanner);
                        break;
                    case 6:
                        unassignCoach(rs, ps, conn, scanner);
                        break;
                    case 7:
                        viewCoachSchedule(rs, ps, conn, scanner);
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

    private void viewCoach(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) {
        System.out.println(": ");
        String query = "SELECT coach.coach_id,person.full_name\n" +
                "FROM coach,person \n" +
                "WHERE coach.person_id=person.person_id;\n";
        try {
            rs = ps.executeQuery(query);
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
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        coachWeeklySchedule(rs, ps, conn, scanner);
                        break;
                    case 2:
                        coachAvailabilitySchedule(rs, ps, conn, scanner);
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

    private void coachAvailabilitySchedule(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) {
        System.out.println("YYYY-MM-DD");
        scanner.nextLine();
        String date = scanner.nextLine();

        System.out.println("Enter 'AM range' or 'PM range': ");
        String time_type = scanner.nextLine();

        String query = "SELECT * FROM coach c WHERE c.coach_id NOT IN (SELECT DISTINCT c1.coach_id \n" +
                "FROM coach c1, coach_assignment, course_schedule, time_type\n" +
                "WHERE c1.coach_id = coach_assignment.coach_id\n" +
                "AND coach_assignment.course_schedule_id = course_schedule.course_schedule_id\n" +
                "AND course_schedule.time_type_id = time_type.time_type_id\n" +
                "AND course_schedule.course_date = ? AND time_type.time_type_value= ?);\n";

        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, date);
            ps.setString(2, time_type);
            System.out.println(ps.toString());
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.printSet(rs);
    }

    private void coachWeeklySchedule(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner){
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
                "AND coach_assignment.course_schedule_id = course_schedule.course_schedule_id\n" +
                "AND course.course_id=course_schedule.course_id \n" +
                "AND course_schedule.time_type_id=time_type.time_type_id \n" +
                "AND course_schedule.course_date >= ? \n" +
                "AND course_schedule.course_date < ? \n" +
                "ORDER BY course_schedule.course_date;";


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

    private void unassignCoach(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter Coach ID");
        int coach_id = scanner.nextInt();

        System.out.println("Enter Schedule ID: ");
        int course_schedule_id = scanner.nextInt();

        String query1 = "DELETE FROM coach_assignment \n" +
                "WHERE coach_id=? \n" +
                "AND course_schedule_id=?;\n";

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

    private void assignCoach(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws  SQLException{
        System.out.println("Enter Schedule ID: ");
        int course_schedule_id = scanner.nextInt();

        System.out.println("Enter Coach ID");
        int coach_id = scanner.nextInt();

        System.out.println("Enter Assigned Role: ");
        String assigned_role = scanner.next();

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

    private void deleteCoach(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws  SQLException{
        System.out.println("Enter Coach ID to Delete: ");
        int coach_id = scanner.nextInt();

        String query1 = "DELETE FROM person \n" +
                "WHERE person_id=(\n" +
                "SELECT person_id \n" +
                "FROM coach \n" +
                "WHERE coach_id=?);";
        String query2 = "DELETE FROM coach_assignment WHERE coach_id=?;";
        String query3 = "DELETE FROM coach WHERE coach_id=?;";

        conn.setAutoCommit(false);
        ps = conn.prepareStatement(query1);
        ps.setInt(1, coach_id);

        PreparedStatement ps2 = null;
        ps2 = conn.prepareStatement(query2);
        ps2.setInt(1, coach_id);

        PreparedStatement ps3 = null;
        ps3 = conn.prepareStatement(query3);
        ps3.setInt(1, coach_id);

        
        ps.executeUpdate();
        try {
            if (true) {
                conn.commit();
                System.out.println("SUCCESS");
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

    private void editCoach(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) {
        int choice;

        while(true) {
            printEditMenu();
            try {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        editAddress(rs, ps, conn, scanner);
                        break;
                    case 2:
                        editDateBirth(rs, ps, conn, scanner);
                        break;
                    case 3:
                        editPhone(rs, ps, conn, scanner);
                        break;
                    case 4:
                        editClassroom(rs, ps, conn, scanner);
                        break;
                    case 5:
                        editDirtBike(rs, ps, conn, scanner);
                        break;
                    case 6:
                        editStreetBike(rs, ps, conn, scanner);
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

    private void editAddress(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Coach Id");
        int coach_id = scanner.nextInt();

        System.out.println("Address");
        scanner.nextLine();
        String address = scanner.nextLine();

        conn.setAutoCommit(false);

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

    private void editDateBirth(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Coach Id");
        int coach_id = scanner.nextInt();

        System.out.println("YYYY-MM-DD");
        java.sql.Date date_birth = java.sql.Date.valueOf(scanner.next());

        conn.setAutoCommit(false);

        String query = "UPDATE person\n" +
                "SET person.date_birth=?\n" +
                "WHERE person.person_id=(\n" +
                "SELECT DISTINCT person_id \n" +
                "FROM coach \n" +
                "WHERE coach_id=?);\n";
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

    private void editPhone(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Coach Id");
        int coach_id = scanner.nextInt();

        System.out.println("Phone");
        scanner.nextLine();
        String phone = scanner.nextLine();

        conn.setAutoCommit(false);

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

    private void editClassroom(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Coach Id");
        int coach_id = scanner.nextInt();

        System.out.println("Classroom Certified");
        int classroom_certified = scanner.nextInt();

        conn.setAutoCommit(false);

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

    private void editDirtBike(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Coach Id");
        int coach_id = scanner.nextInt();

        System.out.println("Dirt Bike Certified");
        int dirtbike_certified = scanner.nextInt();

        conn.setAutoCommit(false);

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

    private void editStreetBike(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Coach Id");
        int coach_id = scanner.nextInt();

        System.out.println("Street Bike Certified");
        int streetbike_certified = scanner.nextInt();

        conn.setAutoCommit(false);

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

    private void createCoach(ResultSet rs, PreparedStatement ps, Connection conn, Scanner scanner) throws SQLException{
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
