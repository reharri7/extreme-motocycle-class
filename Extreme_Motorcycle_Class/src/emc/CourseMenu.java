package emc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This is the courseMenu class
 * 
 * @author Maura
 *
 */
public class CourseMenu {

    /**
     * Holds the menu then lets user select options
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    public void menu(ResultSet rs, PreparedStatement stmt, Connection conn, Scanner scanner) {
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
                System.out.println("Please enter an integer value between 0 and 10" );
                scanner.next();
            }
        }
    }
    

    /**
     * This method asks the user for the course attributes, then sends the create statement to the
     * database.
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void createCourse(ResultSet rs, PreparedStatement stmt, Connection conn, 
            Scanner scanner) {
        
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
            
            try {
                stmt = conn.prepareStatement("INSERT INTO `course` "
                        + "(course_name, course_type, capacity, course_description, cost) " 
                        + "VALUES (?, ?, ?, ?, ?)");
                stmt.setString(1, course_name);
                stmt.setInt(2, course_type);
                stmt.setInt(3, capacity);
                stmt.setString(4, description);
                stmt.setInt(5, cost);
            
                stmt.execute();
                checkCourse(rs, stmt, conn, course_name, course_type, capacity, description, cost);
            } catch(SQLException e) {
                printCreateCourseError(e.getMessage(), rs, stmt, conn, scanner);
            }
            
    }
    
    /**
     * This method prints human readable error statements from the create course method.
     * 
     * @param e
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void printCreateCourseError(String e, ResultSet rs, PreparedStatement stmt, 
            Connection conn, Scanner scanner) {
        System.out.println(e);
        System.out.println("Could not add course");
        if(e.contains("Data truncation: Data too long for column 'course_name'")) {
            System.out.println("course_name too long. Max is 30 characters.");
        } else if(e.contains("Data truncation: Data too long for column 'course_description'")) {
            System.out.println("course_description too long. Max is 150 characters.");
        } else if(e.contains("FOREIGN KEY (`course_type`)")) {
            System.out.println("That course_type does not exist. Please choose from:");
            viewAllCourseType(rs, stmt, conn);
        } else {
            System.out.println(e);
        }
    }
    
    /**
     * This method checks if the course was added to the database.
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param course_name
     * @param course_type
     * @param capacity
     * @param description
     * @param cost
     */
    private void checkCourse(ResultSet rs, PreparedStatement stmt, Connection conn,  
            String course_name, int course_type, int capacity, String description, int cost) {
        try {
            stmt = conn.prepareStatement("SELECT * FROM course WHERE "
                    + "course_name = ?"
                    + " AND course_type = ?"
                    + " AND capacity = ?"
                    + " AND course_description = ?"
                    + " AND cost = ?");
            stmt.setString(1, course_name);
            stmt.setInt(2, course_type);
            stmt.setInt(3, capacity);
            stmt.setString(4, description);
            stmt.setInt(5, cost);
            
            rs = stmt.executeQuery();
    
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
        } catch(SQLException e) {
            System.out.println(e);
        }
    }
    
    /**
     * This course gets the course id, attribue name and new attribute, then sends an edit statement
     * to the database.
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void editCourse(ResultSet rs, PreparedStatement stmt, Connection conn, 
            Scanner scanner) {
        System.out.println("Please enter a number for the course id");
        int course_id = scanner.nextInt();
        
        System.out.println("Please enter the attribute to change");
        String attribute_name = scanner.next();
        
        try {
            if(attribute_name.matches("course_name") 
                    || attribute_name.matches("course_description")){
                
                System.out.println("Please enter the new value");
                String new_value = scanner.next();
                
                stmt = conn.prepareStatement("UPDATE course SET course."+ attribute_name 
                        + "= ? WHERE course_id = ? ");
                stmt.setString(1, new_value);
                stmt.setInt(2, course_id);
                
                stmt.execute();
                
                checkCourseAttribute(rs, stmt, conn, course_id, attribute_name, new_value);
            } else if(attribute_name.matches("capacity") || attribute_name.matches("cost") ||
                    attribute_name.matches("course_type")) {
                System.out.println("Please enter the new value");
                int new_value = scanner.nextInt();
                
                stmt = conn.prepareStatement("UPDATE course SET course." + attribute_name 
                        + " = ? WHERE course_id = ? ");
                stmt.setInt(1, new_value);
                stmt.setInt(2, course_id);
                
                stmt.execute();
                
                checkCourseAttribute(rs, stmt, conn, course_id, attribute_name, 
                        Integer.toString(new_value));
            } else {
                System.out.println("That is not a valid attribute for course");
                return;
            }
            
        } catch(SQLException e) {
            System.out.println(e);
        }
    }
    
    /**
     * This method prints the row
     * 
     * @param rs
     * @param stmt
     * @param conn
     */
    private void printCourseColumn(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            stmt = conn.prepareStatement("SELECT * FROM course");
            
            rs = stmt.executeQuery();
            
            String[] row = new String[6];
            // get metaData
            if(rs != null) {
                
                ResultSetMetaData metaData = rs.getMetaData();
                
                //print column headers
                int numColumns = metaData.getColumnCount();
                for (int i = 1; i <= numColumns; i++) {
                    row[i-1] = metaData.getColumnName(i);
                }
                System.out.println("Course Report:");
                printCourseRow(row);
                System.out.print("----------------------------------------------------"
                        + "-------------------------------------------------------------"
                        + "---------------------------------------------------------\n");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method checks that the edit course was successful
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param course_id
     * @param attribute_name
     * @param new_value
     */
    private void checkCourseAttribute(ResultSet rs, PreparedStatement stmt, Connection conn,
            int course_id, String attribute_name, String new_value ) {
        boolean found = false;
        
        try {
            stmt = conn.prepareStatement("SELECT " + attribute_name 
                    + " FROM course WHERE course_id = ?");
            stmt.setInt(1, course_id);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                found = true;
                if (rs.getObject(1).toString().matches(new_value)) {
                    System.out.println("Success");
                } else {
                    System.out.print("Unable to complete request, please try again");
                }
                System.out.println("");
            }
            if(!found) {
                System.out.println("No Course with that ID. Please select from:");
                viewAllCourses(rs, stmt, conn);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method asks the user for a course id and gets the course from the database and prints it
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void viewCourse(ResultSet rs, PreparedStatement stmt, Connection conn, 
            Scanner scanner) {
        System.out.println("Please enter a number for the course id");
        int course_id = scanner.nextInt();
        boolean found = false;
        try {
            stmt = conn.prepareStatement("SELECT * FROM course WHERE course_id = ?");
            stmt.setInt(1, course_id);
            
            rs = stmt.executeQuery();
            
            
            String[] row = new String[6];
            // get metaData
            if(rs != null) {
                
                ResultSetMetaData metaData = rs.getMetaData();
                
                //print column headers
                int numColumns = metaData.getColumnCount();
                printCourseColumn(rs, stmt, conn);
                //print results
                Object obj = null;
                while (rs.next()) {
                    found = true;
                    for (int i = 1; i <= numColumns; i++) {
                        obj = rs.getObject(i);
                        if (obj != null) {
                            row[i-1] = rs.getObject(i).toString();
                        }
                    } 
                    printCourseRow(row);
                }
                if(!found) {
                    System.out.println("No course exists with that ID. Please select from: ");
                    viewAllCourses(rs, stmt, conn);
                    return;
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method gets all courses from the database and prints all of them.
     * 
     * @param rs
     * @param stmt
     * @param conn
     */
    private void viewAllCourses(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            stmt = conn.prepareStatement("SELECT * FROM course");
            
            rs = stmt.executeQuery();
            
            String[] row = new String[6];
            // get metaData
            if(rs != null) {
                
                ResultSetMetaData metaData = rs.getMetaData();
                
                //print column headers
                int numColumns = metaData.getColumnCount();
                printCourseColumn(rs, stmt, conn);
                //print results
                Object obj = null;
                while (rs.next()) {
                    for (int i = 1; i <= numColumns; i++) {
                        obj = rs.getObject(i);
                        if (obj != null) {
                            row[i-1] = rs.getObject(i).toString();
                        }
                    }
                    printCourseRow(row);
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    /**
     * prints row
     * @param row
     */
    private void printCourseRow(String[] row) {
        System.out.printf("|%-9s|%-30s|%-11s|%-8s|%-100s|%5s|", 
                row[0], 
                row[1], 
                row[2], 
                row[3], 
                row[4], 
                row[5]);
        System.out.println("");
    }
        
    /**
     * This method gets a course id from the user and sends a delete statement to the database
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void deleteCourse(ResultSet rs, PreparedStatement stmt, Connection conn, 
            Scanner scanner) {
        
        try {
            System.out.println("Please enter a number for the course id");
            int course_id = scanner.nextInt();
            
            stmt = conn.prepareStatement("DELETE FROM course WHERE course_id = ?");
            stmt.setInt(1, course_id);
            
            stmt.execute();
            
            checkCourseDelete(rs, stmt, conn, course_id);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    /**
     * This method checks that the course was successfully deleted.
     * @param rs
     * @param stmt
     * @param conn
     * @param course_id
     */
    private void checkCourseDelete(ResultSet rs, PreparedStatement stmt, Connection conn, 
            int course_id)  {
        try {
            stmt = conn.prepareStatement("SELECT * FROM course WHERE course_id = ?");
            stmt.setInt(1, course_id);
            
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println("Success");
            } else {
                System.out.print("Unable to complete request, please try again");
            }
            System.out.println("");
        } catch (SQLException e){
            e.printStackTrace();
        }
        
    }

    /**
     * This method asks the user for a course id and prints the course report with all students
     * and whether they have paid or not.
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void courseReport(ResultSet rs, PreparedStatement stmt, Connection conn, 
            Scanner scanner) {
        boolean found = false;
        
        System.out.println("Please enter a number for the course id");
        int course_id = scanner.nextInt();
        
        try {
            stmt = conn.prepareStatement("SELECT "
                    + "person.full_name, student.student_id, course_enrollment.paid"
                    + " FROM course_enrollment,student,person"
                    + " WHERE course_enrollment.student_id=student.student_id"
                    + " AND student.person_id=person.person_id"
                    + " AND course_enrollment.course_id= ?");
            stmt.setInt(1, course_id);
            
            rs = stmt.executeQuery();
            
            if(rs != null) {
                ResultSetMetaData metaData = rs.getMetaData();
                    
                int numColumns = metaData.getColumnCount();
                String[] strings = new String[3];
                for (int i = 1; i <= numColumns; i++) {
                    if (i <= 3) {
                        strings[i-1] = metaData.getColumnLabel(i);
                    }
                }
                System.out.printf("|%20s|%20s|%12s|", strings[0], strings[1], strings[2]);
                System.out.println("\n--------------------------------------------------------");
            
                while (rs.next()) {
                    found = true;
                    for (int i = 1; i <= numColumns; i++) {
                        if (i <= 3) {
                            strings[i-1] = rs.getObject(i).toString();
                        }
    
                    }
                    System.out.printf("|%20s|%20s|%12s|", strings[0], strings[1], strings[2]);
                                System.out.print("\n");
    
                }
            }
            if(!found) {
                System.out.println("No course with that id. Please choose from:");
                viewAllCourses(rs, stmt, conn);
                return;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    /**
     * This method asks the user for a year a month and a day then prints the list of all courses
     * from this day to a week from this day
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void weeklyCourseReport(ResultSet rs, PreparedStatement stmt, Connection conn, 
            Scanner scanner) {
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
    
    /**
     * This method is a helper method for weeklyCourseReport and prepares and sends the statement
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param thisWeek
     * @param weekLater
     */
    private void executeCourseSchedule(ResultSet rs, PreparedStatement stmt, Connection conn, 
            String thisWeek, String weekLater) {
        try {
            stmt = conn.prepareStatement("SELECT course_schedule.course_date, course.course_id, "
                    + "course.course_name, course.course_type, course.capacity, "
                    + "course.course_description, course.cost "
                    + " FROM course,course_schedule"
                    + " WHERE course.course_id=course_schedule.course_id"
                    + " AND course_schedule.course_date >= ?"
                    + " AND course_schedule.course_date < ?"
                    + " ORDER BY course_schedule.course_date");
            stmt.setString(1,thisWeek);
            stmt.setString(2, weekLater);
            
            rs = stmt.executeQuery();
            
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
                                strings[0], strings[1], strings[2], strings[3], strings[4], 
                                strings[5], strings[6]);
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
            }
        } catch(SQLException e) {
            e.printStackTrace();
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
    
    /**
     * This method is a helper method that converts the day year and month into a string
     * 
     * @param year
     * @param month
     * @param day
     * @return
     */
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
    
    /**
     * This method calculates the date one week after a given year month and day
     * 
     * @param year
     * @param month
     * @param day
     * @return
     */
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
    
    /**
     * This method asks user for a course type value and then creates the course type in the 
     * database
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void createCourseType(ResultSet rs, PreparedStatement stmt, Connection conn, 
            Scanner scanner) {
        System.out.println("Please enter a course type value between 1-30 characters");
        String course_type_value = scanner.next();
        
        try {
            stmt = conn.prepareStatement("INSERT INTO `course_type` "
                    + "(course_type_value) " 
                    + "VALUES (?)");
            stmt.setString(1, course_type_value);
            
            stmt.execute();
            
            checkCourseType(rs, stmt, conn, course_type_value);
        } catch(SQLException e) {
            String msg = e.getMessage();
            if(msg.contains("Data too long for column 'course_type_value'")){
                System.out.println("course_type_value is too long. Max length 30 characters.");
            }
        }
    }
    
    /**
     * This method checks that the course type was successfully created.
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param course_type_value
     */
    private void checkCourseType(ResultSet rs, PreparedStatement stmt, Connection conn, 
            String course_type_value) {
        try {
            stmt = conn.prepareStatement("SELECT course_type_value FROM course_type WHERE "
                    + "course_type_value = ?");
            stmt.setString(1, course_type_value);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                if (rs.getObject(1).toString().matches(course_type_value)) {
                    System.out.println("Success");
                    return;
                } else {
                    System.out.print("Unable to complete request, please try again");
                }
                System.out.println("");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method asks the user for a course type id and a coure type value then updates that 
     * course type with the new course type value
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void editCourseType(ResultSet rs, PreparedStatement stmt, Connection conn, 
            Scanner scanner) {
        try {
            System.out.println("Please enter a number for the course_type_id");
            int course_type_id = scanner.nextInt();
            
            System.out.println("Please enter the new value for course_type_value");
            String new_value = scanner.next();
            
            stmt = conn.prepareStatement("UPDATE course_type SET course_type.course_type_value = ? "
                    + "WHERE course_type_id = ? ");
            stmt.setString(1, new_value);
            stmt.setInt(2, course_type_id);
            
            stmt.execute();
            
            checkCourseTypeAttribute(rs, stmt, conn, course_type_id, new_value);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * This method checks that the course type was successfully edited.
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param course_type_id
     * @param new_value
     */
    private void checkCourseTypeAttribute(ResultSet rs, PreparedStatement stmt, Connection conn, 
            int course_type_id, String new_value ) {
        boolean found = false;
        
        try {
            stmt = conn.prepareStatement("SELECT course_type_value FROM course_type WHERE " 
                    + "course_type_id = ?");
            stmt.setInt(1, course_type_id);
            
            rs = stmt.executeQuery();
    
            while (rs.next()) {
                found = true;
                if (rs.getObject(1).toString().matches(new_value)) {
                    System.out.println("Success");
                } else {
                    System.out.print("Unable to complete request, please try again");
                }
                System.out.println("");
            }
            
            if(!found) {
                System.out.println("No course_type with that id. Please chose from:");
                viewAllCourseType(rs, stmt, conn);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * this method asks the user for a course type id and then prints out that course type.
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void viewCourseType(ResultSet rs, PreparedStatement stmt, Connection conn, 
            Scanner scanner) {
        System.out.println("Please enter a number for the course_type_id");
        int course_type_id = scanner.nextInt();
        boolean found = false;
        boolean header = true;
        try {
            stmt = conn.prepareStatement("SELECT * FROM course_type WHERE "
                    + "course_type_id = ?");
            stmt.setInt(1, course_type_id);
            
            rs = stmt.executeQuery();
            
            String[] row = new String[6];
            int numColumns = 0;
            
            if(rs != null) {
                //print results
                Object obj = null;
                while (rs.next()) {
                    found = true;
                    if(header) {
                        // get metaData
                        ResultSetMetaData metaData = rs.getMetaData();

                        // print column headers
                        numColumns = metaData.getColumnCount();
                        for (int i = 1; i <= numColumns; i++) {
                            row[i - 1] = metaData.getColumnName(i);
                        }

                        printCourseTypeRow(row);
                        System.out.print("-----------------------------------------------\n");
                        header = false;
                    }
                    for (int i = 1; i <= numColumns; i++) {
                        obj = rs.getObject(i);
                        if (obj != null) {
                            row[i-1] = rs.getObject(i).toString();
                        }
                    }
                    printCourseTypeRow(row);
                }
                
                
            }
            
            if(!found) {
                System.out.println("No course_type with that course_type_id. Please choose from:");
                viewAllCourseType(rs, stmt, conn);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method prints out all course types
     * 
     * @param rs
     * @param stmt
     * @param conn
     */
    private void viewAllCourseType(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            stmt = conn.prepareStatement("SELECT * FROM course_type");
            
            rs = stmt.executeQuery();
            
            String[] row = new String[6];
            // get metaData
            if(rs != null) {
                
                ResultSetMetaData metaData = rs.getMetaData();
                
                //print column headers
                int numColumns = metaData.getColumnCount();
                for (int i = 1; i <= numColumns; i++) {
                    row[i-1] = metaData.getColumnName(i);
                }
                
                printCourseTypeRow(row);
                System.out.print("-----------------------------------------------\n");
                //print results
                Object obj = null;
                while (rs.next()) {
                    for (int i = 1; i <= numColumns; i++) {
                        obj = rs.getObject(i);
                        if (obj != null) {
                            row[i-1] = rs.getObject(i).toString();
                        }
                    }
                    printCourseTypeRow(row);
                }
            }
        } catch( SQLException e) {
            e.printStackTrace();
        }
            
    }
    
    /**
     * print row
     * 
     * @param row
     */
    private void printCourseTypeRow(String[] row) {
        System.out.printf("|%-14s|%-30s|", 
                row[0], 
                row[1]);
        System.out.println("");
    }
    
    /**
     * Asks the user for a course type id and deletes that course type from the database.
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void deleteCourseType(ResultSet rs, PreparedStatement stmt, Connection conn, 
            Scanner scanner)  {
        System.out.println("Please enter a number for the course_type_id");
        int course_type_id = scanner.nextInt();
        
        try {
            stmt = conn.prepareStatement("DELETE FROM course_type WHERE course_type_id = ?");
            stmt.setInt(1, course_type_id);
            
            stmt.execute();
    
            checkCourseTypeDelete(rs, stmt, conn, course_type_id);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks that the course type was deleted.
     * 
     * @param rs
     * @param stmt
     * @param conn
     * @param course_type_id
     */
    private void checkCourseTypeDelete(ResultSet rs, PreparedStatement stmt, Connection conn, 
            int course_type_id) {
        try {
            stmt = conn.prepareStatement("SELECT * FROM course_type WHERE "
                    + "course_type_id = ?");
            stmt.setInt(1, course_type_id);
            
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println("Success");
            } else {
                System.out.print("Unable to complete request, please try again");
            }
            System.out.println("");
        } catch(SQLException e) {
            e.printStackTrace();
        }

    }
        
    /**
     * prints the CourseMenu
     */
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