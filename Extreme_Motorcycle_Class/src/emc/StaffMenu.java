package emc;

/**
 * StaffMenu.java
 *
 * Prints and handles Staff sub menu.
 *
 * Group 4
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    private void viewCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println(": ");
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT coach.coach_id,person.full_name\n" +
                    "FROM coach,person \n" +
                    "WHERE coach.person_id=person.person_id;\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.printSet(rs);
    }

    private void viewCoachSchedule(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void unassignCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void assignCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void deleteCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void editCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void createCoach(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

}
