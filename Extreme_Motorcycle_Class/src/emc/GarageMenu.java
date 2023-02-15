package emc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
                
                case 0: return;
                }
                
            }catch (InputMismatchException ex){
                System.out.println("Please enter an integer value between 0 and (TODO:)" );
                scanner.next();
            }
        }
    }

}
