package emc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Extreme Motorcycle Class Database Management");
        if (args.length != 4) {
            System.out.println("USAGE: java emc.Main <url> <username> <password> <driver>");
            System.exit(0);
        }

        ResultSet rs = null;
        Statement stmt = null;
        PreparedStatement pStmt = null;
        Connection conn = null;
        
        CourseMenu courseMenu = new CourseMenu();
        StudentMenu studentMenu = new StudentMenu();
        GarageMenu garageMenu = new GarageMenu();
        StaffMenu staffMenu = new StaffMenu();
        InfrastructureMenu infMenu = new InfrastructureMenu();

        try {
            Class.forName(args[3]);

            conn = DriverManager.getConnection(args[0], args[1], args[2]);

            
            
            try (Scanner scanner = new Scanner(System.in)) {
                int selection;
                
                while(true) {
                    printMenu();
                    try {
                        selection = scanner.nextInt();
                        switch (selection){
                        case 1: courseMenu.menu(rs, pStmt, conn, scanner); break;
                        case 2: studentMenu.menu(rs, stmt, conn, scanner); break;
                        case 3: garageMenu.menu(rs, stmt, conn, scanner); break;
                        case 4: staffMenu.menu(rs, stmt, conn, scanner); break;
                        case 5: infMenu.menu(rs, stmt, conn, scanner); break;
                        case 0: System.exit(0);
                        }
                        
                    }catch (InputMismatchException ex){
                        System.out.println("Please enter an integer value between 0 and 5" );
                        scanner.next();
                    }
                    
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
            
        } catch (Exception exc) {
            exc.printStackTrace();
            System.exit(0);
        } finally {
            try {
                rs.close();
                stmt.close();
            } catch (Throwable t) {
            } 
            
            try {
                conn.close();
            } catch (Throwable t2) {
                System.err.println("Uh-oh! Connection leaked!");
            }
        }
    }
    
    public static void printMenu() {
            System.out.println("Main Menu");
            System.out.println("1. Course");
            System.out.println("2. Student");
            System.out.println("3. Garage");
            System.out.println("4. Staff");
            System.out.println("5. Infrastructure");
            System.out.println("0. Exit");
    }
}
