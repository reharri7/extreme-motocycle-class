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
