package emc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GarageMenu {

    public void menu(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        int selection;
        
        while(true) {
            printMenu();
            try {
                selection = scanner.nextInt();
                switch (selection){
                case 1: createBike(rs, stmt, conn, scanner); break;
                case 2: viewBike(rs, stmt, conn, scanner); break;
                case 3: editBike(rs, stmt, conn, scanner); break;
                case 4: deleteBike(rs, stmt, conn, scanner); break;
                case 5: assignBike(rs, stmt, conn, scanner); break;
                case 6: unassignBike(rs, stmt, conn, scanner); break;
                case 7: bikeProblemReport(rs, stmt, conn, scanner); break;
                case 8: bikeAssignmentReport(rs, stmt, conn, scanner); break;
                case 9: createBike(rs, stmt, conn, scanner); break;
                case 10: viewBike(rs, stmt, conn, scanner); break;
                case 11: editBike(rs, stmt, conn, scanner); break;
                case 12: deleteBikeProblem(rs, stmt, conn, scanner); break;
                case 13: createBikeType(rs, stmt, conn, scanner); break;
                case 14: viewBikeType(rs, stmt, conn, scanner); break;
                case 15: editBikeType(rs, stmt, conn, scanner); break;
                case 16: deleteBikeType(rs, stmt, conn, scanner); break;
                case 0: return;
                }
                
            }catch (InputMismatchException ex){
                System.out.println("Please enter an integer value between 0 and (TODO:)" );
                scanner.next();
            }
        }
    }

    private void printMenu() {
    }

    private void deleteBikeType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void editBikeType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void viewBikeType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void createBikeType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void deleteBikeProblem(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void bikeAssignmentReport(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void bikeProblemReport(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void unassignBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void assignBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void deleteBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void editBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void viewBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

    private void createBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
    }

}
