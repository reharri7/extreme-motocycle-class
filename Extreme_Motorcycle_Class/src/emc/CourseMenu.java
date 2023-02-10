package emc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CourseMenu {

    public void menu(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        int selection;
        
        while(true) {
            printMenu();
            try {
                selection = scanner.nextInt();
                switch (selection){
                case 1: createCourse(rs, stmt, conn, scanner); break;
                case 2: editCourse(rs, stmt, conn, scanner); break;
                case 3: deleteCourse(rs, stmt, conn, scanner); break;
                case 4: courseReport(rs, stmt, conn, scanner); break;
                case 5: weeklyCourseReport(rs, stmt, conn, scanner); break;
                case 6: createCourseType(rs, stmt, conn, scanner); break;
                case 7: editCourseType(rs, stmt, conn, scanner); break;
                case 8: viewCourseType(rs, stmt, conn, scanner); break;
                case 9: deleteCourseType(rs, stmt, conn, scanner); break;
                case 0: return;
                }
                
            }catch (InputMismatchException ex){
                System.out.println("Please enter an integer value between 0 and 9" );
                scanner.next();
            }
        }
    }
    
    private void createCourse(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) 
            throws SQLException {
        
            System.out.println("Please enter a course name between 1-30 characters");
            String course_name = scanner.next();
            
            System.out.println("Please enter a number for course type id");
            int course_type = scanner.nextInt();
            
            System.out.println("Please enter a number for the capacity");
            int capacity = scanner.nextInt();
            
            System.out.println("Please enter the course description between 1-150 characters");
            String description = scanner.next();
            
            System.out.println("Please enter a number for the cost");
            int cost = scanner.nextInt();
            
            String create = "INSERT INTO `course` "
                    + "(course_name, course_type, capacity, course_description, cost) VALUES ('"
                    + course_name + "', "
                    + course_type + ", "
                    + capacity + ", '"
                    + description + "', "
                    + cost + ")";
            
            stmt = conn.createStatement();
            stmt.executeUpdate(create);
            checkCourse(rs, stmt, conn, course_name, course_type, capacity, description, cost);
    }
    


    private void editCourse(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        // TODO Auto-generated method stub
        
    }
    
    private void checkCourse(ResultSet rs, Statement stmt, Connection conn,  String course_name, 
            int course_type, int capacity, String description, int cost) throws SQLException {
        String select = "SELECT * FROM course WHERE "
                + "course_name = '" + course_name
                + "' AND course_type = " + course_type
                + " AND capacity = " + capacity
                + " AND course_description = '" + description
                + "' AND cost = " + cost;
        
        stmt = conn.createStatement();
        rs = stmt.executeQuery(select);
        
        ResultSetMetaData metaData = rs.getMetaData();

        int numColumns = metaData.getColumnCount();
        
        Object obj = null;
        while (rs.next()) {
            if (rs.getObject(2).toString().matches(course_name)
                    && rs.getObject(3).toString().matches(Integer.toString(course_type))
                    && rs.getObject(4).toString().matches(Integer.toString(capacity))
                    && rs.getObject(5).toString().matches(description)
                    && rs.getObject(6).toString().matches(Integer.toString(cost))) {
                System.out.println("Success");
            } else {
                System.out.print("Unable to complete request, please try again");
            }
            System.out.println("");
        }
    }
    
    private void deleteCourse(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        // TODO Auto-generated method stub
        
    }
    
        
    private void courseReport(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        // TODO Auto-generated method stub
        
    }
    
    private void weeklyCourseReport(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        // TODO Auto-generated method stub
        
    }
    
    private void createCourseType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        // TODO Auto-generated method stub
        
    }
    
    private void editCourseType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        // TODO Auto-generated method stub
        
    }   
    
    private void viewCourseType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        // TODO Auto-generated method stub
        
    }
    
    private void deleteCourseType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        // TODO Auto-generated method stub
        
    }

    private void printMenu() {
        System.out.println("Course Menu");
        System.out.println("1. Create Course");
        System.out.println("2. Edit Course");
        System.out.println("3. Delete Course");
        System.out.println("4. Course(Report)");
        System.out.println("5. Weekly Schedule of Course (Report)");
        System.out.println("6. Create Course Type");
        System.out.println("7. Edit Course Type");
        System.out.println("8. View Course Type");
        System.out.println("9. Delete Course Type");
        System.out.println("0. Main Menu");
    }

}
