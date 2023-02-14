package emc;

/**
 * CourseMenu.java
 *
 * Prints and handles Course sub menu.
 *
 * Group 4
 */

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
                case 3: viewCourse(rs, stmt, conn, scanner); break;
                case 4: deleteCourse(rs, stmt, conn, scanner); break;
                case 5: courseReport(rs, stmt, conn, scanner); break;
                case 6: weeklyCourseReport(rs, stmt, conn, scanner); break;
                case 7: createCourseType(rs, stmt, conn, scanner); break;
                case 8: editCourseType(rs, stmt, conn, scanner); break;
                case 9: viewCourseType(rs, stmt, conn, scanner); break;
                case 10: deleteCourseType(rs, stmt, conn, scanner); break;
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
    
    private void editCourse(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) 
            throws SQLException {
        System.out.println("Please enter a number for the course id");
        int course_id = scanner.nextInt();
        
        System.out.println("Please enter the attribute to change");
        String attribute_name = scanner.next();
        
        System.out.println("Please enter the new value");
        String new_value = scanner.next();
        
        String update = "UPDATE course "
                + "SET "
                + attribute_name + " = "
                + new_value
                + " WHERE course_id = "
                + course_id;
        
        stmt = conn.createStatement();
        stmt.executeUpdate(update);
        
        checkCourseAttribute(rs, stmt, conn, course_id, attribute_name, new_value);
    }
    
    private void checkCourseAttribute(ResultSet rs, Statement stmt, Connection conn, int course_id,
            String attribute_name, String new_value ) throws SQLException {
        String select = "SELECT " + attribute_name
                + " FROM course"
                + " WHERE course_id = " + course_id;
        
        stmt = conn.createStatement();
        rs = stmt.executeQuery(select);
        
        ResultSetMetaData metaData = rs.getMetaData();

        int numColumns = metaData.getColumnCount();
        
        Object obj = null;
        while (rs.next()) {
            if (rs.getObject(1).toString().matches(new_value)) {
                System.out.println("Success");
            } else {
                System.out.print("Unable to complete request, please try again");
            }
            System.out.println("");
        }
    }
    
    private void viewCourse(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        // TODO Auto-generated method stub
        
    }
        
    private void deleteCourse(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) 
            throws SQLException {
        System.out.println("Please enter a number for the course id");
        int course_id = scanner.nextInt();
        
        String update = "DELETE"
                + " FROM course"
                + " WHERE course_id = "
                + course_id;
        
        stmt = conn.createStatement();
        stmt.executeUpdate(update);
        
        checkCourseDelete(rs, stmt, conn, course_id);
    }
    
        
    private void checkCourseDelete(ResultSet rs, Statement stmt, Connection conn, int course_id) 
            throws SQLException {
        String select = "SELECT *"
                + " FROM course"
                + " WHERE course_id = " + course_id;
        
        stmt = conn.createStatement();
        rs = stmt.executeQuery(select);
        
        ResultSetMetaData metaData = rs.getMetaData();

        int numColumns = metaData.getColumnCount();
        
        Object obj = null;
        if (!rs.next()) {
            System.out.println("Success");
        } else {
            System.out.print("Unable to complete request, please try again");
        }
        System.out.println("");
        
    }

    private void courseReport(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) 
            throws SQLException {
        System.out.println("Please enter a number for the course id");
        int course_id = scanner.nextInt();
        
        String query = "SELECT person.full_name, student.student_id, course_enrollment.paid"
                + " FROM course_enrollment,student,person"
                + " WHERE course_enrollment.student_id=student.student_id"
                + " AND student.person_id=person.person_id"
                + " AND course_enrollment.course_id= "
                + course_id;
        
        stmt = conn.createStatement();
        rs = stmt.executeQuery(query);
        
        if(rs != null) {
            ResultSetMetaData metaData = rs.getMetaData();
                
            int numColumns = metaData.getColumnCount();
            String[] strings = new String[3];
            for (int i = 1; i <= numColumns; i++) {
                if (i <= 3) {
                    strings[i-1] = metaData.getColumnLabel(i);

                }

                if (i >= 3) {
                    System.out.printf("|%20s|%20s|%12s|", strings[0], strings[1], strings[2]);
                    System.out.println("\n-------------------------------" 
                    + "-----------------------------");
                }
            }
        
            Object obj = null;
            while (rs.next()) {
                for (int i = 1; i <= numColumns; i++) {
                    obj = rs.getObject(i);
                    if (obj != null) {
                        if (i <= 3) {
                            strings[i-1] = obj.toString();
                        }
                        if (i >= 3) {
                            System.out.printf("|%20s|%20s|%12s|", strings[0], strings[1], strings[2]);
                            System.out.print("\n");
                        }
                    }

                }

            }
        } 
    }
    
    private void weeklyCourseReport(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) 
            throws SQLException {
        boolean correctInput = false;
        int year = 0;
        int month = 0;
        int day = 0;
        
        // get year
        while(!correctInput) {
            System.out.println("Please enter a year in the format yyyy"
                    + " or 0 to go back to CourseMenu");
            year = scanner.nextInt();
            if(year < 10000 && year > 999) {
                correctInput = true;
            }
            if(year == 0) {
                return;
            }
        }
        
        // get month
        correctInput = false;
        while(!correctInput) {
            System.out.println("Please enter a number for the month"
                    + " or 0 to go back to CourseMenu");
            month = scanner.nextInt();
            if(month < 13 && month > 0) {
                correctInput = true;
            }
            if(month == 0) {
                return;
            }
        }
        
        // get day
        correctInput = false;
        while(!correctInput) {
            System.out.println("Please enter a number for the day"
                    + " or 0 to go back to CourseMenu");
            day = scanner.nextInt();
            if(day <= getDayMax(month, year) && day > 0) {
                correctInput = true;
            }
            if(day == 0) {
                return;
            }
        }
        // convert to string
        String thisWeek =convertDateToString(year, month, day);
       
        // get week later
        String weekLater = calculateWeekLater(year,month, day);
        if(weekLater == null) {
            return;
        }else {
            System.out.println("Schedule for week " + thisWeek +" to " 
                + weekLater + ":");
        }
        
        // send query and print results
        executeCourseSchedule(rs, stmt, conn, thisWeek, weekLater);
    }
    
    private void executeCourseSchedule(ResultSet rs, Statement stmt, Connection conn, 
            String thisWeek, String weekLater) throws SQLException {
        String query = "SELECT course_schedule.course_date, course.course_id, course.course_name, "
                + "course.course_type, course.capacity, course.course_description, course.cost"
                + " FROM course,course_schedule"
                + " WHERE course.course_id=course_schedule.course_id"
                + " AND course_schedule.course_date >= '" + thisWeek + "'"
                + " AND course_schedule.course_date < '" + weekLater + "'"
                + " ORDER BY course_schedule.course_date";
        
        stmt = conn.createStatement();
        rs = stmt.executeQuery(query);
        
        if(rs != null) {
            ResultSetMetaData metaData = rs.getMetaData();
                
            int numColumns = metaData.getColumnCount();
            String[] strings = new String[7];
            for (int i = 1; i <= numColumns; i++) {
                if (i <= 7) {
                    strings[i-1] = metaData.getColumnLabel(i);

                }

                if (i >= 7) {
                    System.out.printf("|%-11s|%-9s|%-30s|%-11s|%-8s|%-100s|%5s|", 
                            strings[0], strings[1], strings[2], strings[3], strings[4], strings[5], 
                            strings[6]);
                    System.out.println("\n-------------------------------------------" 
                    + "-----------------------------------------------"
                    + "-----------------------------------------------"
                    + "---------------------------------------------");
                }
            }
        
            Object obj = null;
            while (rs.next()) {
                for (int i = 1; i <= numColumns; i++) {
                    obj = rs.getObject(i);
                    if (obj != null) {
                        if (i <= 7) {
                            strings[i-1] = obj.toString();
                        }
                        if (i >= 7) {
                            System.out.printf("|%-11s|%-9s|%-30s|%-11s|%-8s|%-100s|%5s|", 
                                    strings[0], strings[1], strings[2], strings[3], strings[4], 
                                    strings[5], strings[6]);
                            System.out.print("\n");
                        }
                    }

                }

            }
        }else {
            System.out.println("no");
        }
    }
    
    private int getDayMax(int month, int year) {
        if(month == 2 ) {
            if(year%4 == 0) {
                return 29;
            }else {
                return 28;
            }
        }
        if(month <=7) {
            if(!(month%2 == 0)) {
                return 31;
            }else {
                return 30;
            }
        }else {
            if(!(month%2 == 0)) {
                return 30;
            }else {
                return 31;
            }
        }
    }
    
    private String convertDateToString(int year, int month, int day) {
        String date = Integer.toString(year)+ "-";
        if(month < 10) {
            date = date + "0" + Integer.toString(month)+ "-";
        }else {
            date = date + Integer.toString(month) + "-";
        }
        
        if(day < 10) {
            date = date + "0" + Integer.toString(day);
        }else {
            date = date + Integer.toString(day);
        }
        return date;
    }
    
    private String calculateWeekLater(int year, int month, int day) {
        int newDay = day + 7;
        int newMonth = month;
        int newYear = year;
        int max = getDayMax(month, year);
        if(newDay > max) {
            newDay = newDay - max; 
            newMonth++;
        }
        if(newMonth > 12) {
            newMonth = 1;
            newYear++;
        }
        if(newYear >= 10000) {
            System.out.println("Both dates must be before year 10000");
            return null;
        }else {
            return convertDateToString(newYear, newMonth, newDay);
        }
        
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
        System.out.println("3. View Course");
        System.out.println("4. Delete Course");
        System.out.println("5. Course(Report)");
        System.out.println("6. Weekly Schedule of Course (Report)");
        System.out.println("7. Create Course Type");
        System.out.println("8. Edit Course Type");
        System.out.println("9. View Course Type");
        System.out.println("10. Delete Course Type");
        System.out.println("0. Main Menu");
    }

}
