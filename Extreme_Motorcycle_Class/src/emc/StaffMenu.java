package emc;

/**
 * StaffMenu.java
 *
 * Prints and handles Staff sub menu.
 *
 * Group 4
 */

import java.sql.*;
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
    }

    private void coachWeeklySchedule(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void unassignCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void assignCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void deleteCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void editCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void createCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException{
        System.out.println("Name");
        String full_name = scanner.next();

        System.out.println("adress");
        String address = scanner.next();

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
        conn.setAutoCommit(false);
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
