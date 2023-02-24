package emc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GarageMenu {

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     * @throws SQLException
     */
    public void menu(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) throws SQLException {
        int selection;

        while (true) {
            printMenu();
            try {
                selection = scanner.nextInt();
                switch (selection) {
                    case 1:
                        createBike(rs, stmt, conn, scanner);
                        break;
                    case 2:
                        viewBike(rs, stmt, conn, scanner);
                        break;
                    case 3:
                        editBike(rs, stmt, conn, scanner);
                        break;
                    case 4:
                        deleteBike(rs, stmt, conn, scanner);
                        break;
                    case 5:
                        assignBike(rs, stmt, conn, scanner);
                        break;
                    case 6:
                        unassignBike(rs, stmt, conn, scanner);
                        break;
                    case 7:
                        bikeProblemReport(rs, stmt, conn, scanner);
                        break;
                    case 8:
                        bikeAssignmentReport(rs, stmt, conn, scanner);
                        break;
                    case 9:
                        createBikeProblem(rs, stmt, conn, scanner);
                        break;
                    case 10:
                        viewBikeProblem(rs, stmt, conn, scanner);
                        break;
                    case 11:
                        editBikeProblem(rs, stmt, conn, scanner);
                        break;
                    case 12:
                        deleteBikeProblem(rs, stmt, conn, scanner);
                        break;
                    case 13:
                        createBikeType(rs, stmt, conn, scanner);
                        break;
                    case 14:
                        viewBikeTypes(rs, stmt, conn, scanner);
                        break;
                    case 15:
                        editBikeType(rs, stmt, conn, scanner);
                        break;
                    case 16:
                        deleteBikeType(rs, stmt, conn, scanner);
                        break;
                    case 0:
                        return;
                }

            } catch (InputMismatchException ex) {
                System.out.println("Please enter an integer value between 0 and 16" );
                scanner.next();
            }
        }
    }

    private void printMenu() {
        System.out.println("Garage Menu");
        System.out.println("1. Create Bike");
        System.out.println("2. View Bikes");
        System.out.println("3. Edit Bike");
        System.out.println("4. Delete Bike");
        System.out.println("5. Assign Bike");
        System.out.println("6. Unassign Bike");
        System.out.println("7. Bike Problem Report");
        System.out.println("8. Bike Assignment Report");
        System.out.println("9. Create Bike Problem");
        System.out.println("10. View Bike Problems");
        System.out.println("11. Edit Bike Problem");
        System.out.println("12. Delete Bike Problem");
        System.out.println("13. Create Bike Type");
        System.out.println("14. View Bike Types");
        System.out.println("15. Edit Bike Type");
        System.out.println("16. Delete Bike Type");
        System.out.println("0. Main Menu");
    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void deleteBikeType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike type's ID: ");
        int bikeTypeID = scanner.nextInt();

        String query = "DELETE FROM bike_type WHERE bike_type_id=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, bikeTypeID);
            ps.executeUpdate();
            System.out.println("Bike type deleted successfully");
        } catch (SQLException e) {
            System.out.println("Query failed. Please ensure the bike type id exists.");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }
    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void editBikeType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike type's ID: ");
        int bikeTypeID = scanner.nextInt();

        System.out.println("Please enter the attribute you wish to change: ");
        String attribute = scanner.next();

        System.out.println("Please enter the new value for the attribute: ");
        String newValue = scanner.next();

        /* Make a switch case to determine whether to cast the input to another type */
        String[] validAttributes = {"bike_type_id", "bike_type_value"};
        boolean validAttribute = false;
        for (String s : validAttributes) {
            if (s.equals(attribute)) {
                validAttribute = true;
                break;
            }
        }

        if (!validAttribute) {
            System.out.println("Invalid attribute");
            return;
        }

        String query = "UPDATE bike_type SET " + attribute + "=? WHERE bike_type_id=?";

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, newValue);
            ps.setInt(2, bikeTypeID);
            ps.executeUpdate();
            System.out.println("Bike type updated successfully");
        } catch (SQLException e) {
            System.out.println("Query failed. Please ensure input data is valid.");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }
    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void viewBikeTypes(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Here are all the bike types registered:");
        String query = "SELECT * FROM bike_type";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            Utils.printSet(rs);
        }
        catch (SQLException e) {
            System.out.println("Query failed. Try again.");
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }
    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void createBikeType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a name for the bike type between 1 and 10 characters: ");
        String bikeTypeName = scanner.next();

        String query = "INSERT INTO bike_type (bike_type_value) VALUES (?)";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, bikeTypeName);
            ps.executeUpdate();
            System.out.println("Bike type created successfully.");
        } catch (SQLException e) {
            System.out.println("Query failed. Please check that the bike type name is valid.");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }
    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void deleteBikeProblem(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the problem ID: ");
        int problemID = scanner.nextInt();

        String query = "DELETE FROM problem WHERE problem_id=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, problemID);
            ps.executeUpdate();
            System.out.println("Problem " + problemID + " deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Query failed. Please check that the problem ID is valid.");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }
    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void editBikeProblem(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike's ID: ");
        int bikeID = scanner.nextInt();

        System.out.println("Please enter the attribute you wish to change: ");
        String attribute = scanner.next();

        System.out.println("Please enter the new value for the attribute: ");
        String newValue = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            newValue = br.readLine();
        } catch (IOException e) {
            System.out.println("Failed to read input.");
            return;
        }
        
        // We check for valid attributes here to avoid SQL injection and ensure we are only changing things we want to be changed.
        String[] validAttributes = {"problem_id", "problem_date", "bike_id", "repair_date", "description", "cost"};
        boolean validAttribute = false;
        for (String s : validAttributes) {
            if (s.equals(attribute)) {
                validAttribute = true;
                break;
            }
        }

        if (!validAttribute) {
            System.out.println("Invalid attribute");
            return;
        }

        String query = "UPDATE problem SET " + attribute + "=? WHERE bike_id=?";
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, newValue);
            ps.setInt(2, bikeID);
            ps.executeUpdate();
            System.out.println(attribute + " successfully updated for bike " + bikeID + ".");
        }
        catch (SQLException e) {
            System.out.println("Query failed. Ensure the bike ID is correct and try again.");
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }
    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void viewBikeProblem(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        String query = "SELECT * FROM problem";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            Utils.printSet(rs);
        }
        catch (SQLException e) {
            System.out.println("Query failed. Try again.");
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }
    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void createBikeProblem(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike's ID: ");
        int bikeID = scanner.nextInt();

        System.out.println("Please enter a date the problem occured (yyyy-mm-dd): ");
        String date = scanner.next();

        System.out.println("Please enter a description of the problem between 1 and 150 characters: ");
        String description = "";
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            description = reader.readLine();
        } catch (IOException e) {
            System.out.println("Failed to close prepared statement, leaking resources.");
        }

        System.out.println("Please enter a date to specify the repair date (yyyy-mm-dd): ");
        String repairDate = scanner.next();

        System.out.println("Please enter a number for the cost of repair: ");
        int cost = scanner.nextInt();

        String query = "INSERT INTO problem (problem_date, bike_id, repair_date, description, cost) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, date);
            ps.setInt(2, bikeID);
            ps.setString(3, repairDate);
            ps.setString(4, description);
            ps.setInt(5, cost);
            ps.executeUpdate();
            System.out.println("Sucessfully created bike problem");
        } 
        catch (SQLException e) {
            System.out.println("Query failed.  Ensure you entered a valid date and did not exceed the character limit for the description.");
        } 
        
        finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }
    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void bikeAssignmentReport(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bikes ID: ");
        int bikeID = scanner.nextInt();

        String query = "SELECT course_schedule.course_date,course.course_name "
            + "FROM bike_assignment,course_schedule,course "
            + "WHERE bike_assignment.course_schedule_id=course_schedule.course_schedule_id "
            + "AND course_schedule.course_id=course.course_id "
            + "AND bike_assignment.bike_id=?";

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, bikeID);
            rs = ps.executeQuery();
            Utils.printSet(rs);
        }
        catch (SQLException e) {
            System.out.println("Query failed.  Ensure you entered a valid bike ID.");
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }

    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void bikeProblemReport(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike's ID: ");
        int bikeID = scanner.nextInt();

        String query = "SELECT problem_date,repair_date,description,cost FROM problem WHERE bike_id=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, bikeID);
            rs = ps.executeQuery();
            Utils.printSet(rs);
        }
        catch (SQLException e) {
            System.out.println("Query failed.  Ensure you entered a valid bike ID.");
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close prepared statement, leaking resources.");
            }
        }
    }


    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void unassignBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike assignment ID: ");
        int bikeAssignmentID = scanner.nextInt();

        PreparedStatement ps = null;
        String query = "DELETE FROM bike_assignment WHERE bike_assignment_id = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, bikeAssignmentID);
            ps.executeUpdate();
            System.out.println("Sucessfully unassigned the bike assignment: " + bikeAssignmentID);
        } catch (SQLException e) {
            System.out.println("Query failed.  Ensure you entered a valid bike assignment ID.");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }

    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void assignBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike's ID: ");
        int bikeID = scanner.nextInt();

        System.out.println("Please a number for the course schedule's ID: ");
        int courseScheduleID = scanner.nextInt();

        PreparedStatement ps = null;
        String query = "INSERT INTO bike_assignment (course_schedule_id, bike_id) VALUES (?, ?)";

        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, courseScheduleID);
            ps.setInt(2, bikeID);
            ps.executeUpdate();
            System.out.println("Sucessfully assigned bike " + bikeID + " to course schedule " + courseScheduleID);
        } catch (SQLException e) {
            System.out.println("Query failed.  Ensure you entered a valid bike ID and course schedule ID.");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }

    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void deleteBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike's ID you wish to delete: ");
        int bikeID = scanner.nextInt();

        // Delete from bike
        String query = "DELETE FROM bike WHERE bike_id=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, bikeID);
            ps.executeUpdate();
            System.out.println("Sucessfully deleted bike: " + bikeID);
        } catch (SQLException e) {
            System.out.println("Query failed.  Ensure you entered a valid bike ID.");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }

        // Delete any references to the bike in bike_assignment
        query = "DELETE FROM bike_assignment WHERE bike_id=?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, bikeID);
            ps.executeUpdate();
            System.out.println("Sucessfully deleted assignments for bike: " + bikeID);
        } catch (SQLException e) {
            System.out.println("Query failed.  Ensure you entered a valid bike ID.");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }
    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void editBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike's ID you wish to edit: ");
        int bikeID = scanner.nextInt();

        System.out.println("Please enter the attribute you wish to edit: ");
        String attribute = scanner.next();

        System.out.println("Please enter the new value for the attribute: ");
        String newValue = scanner.next();

        /* Make a switch case to determine whether to cast the input to another type */
        String[] validAttributes = {"bike_id", "brand", "type", "license_plate", "vin", "cc", "broken"};
        boolean validAttribute = false;
        for (String s : validAttributes) {
            if (s.equals(attribute)) {
                validAttribute = true;
            }
        }

        if (!validAttribute) {
            System.out.println("Invalid attribute");
            return;
        }

        String query = "UPDATE bike SET " + attribute + "=? WHERE bike_id=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, newValue);
            ps.setInt(2, bikeID);
            ps.executeUpdate();
            System.out.println("Sucessfully updated bike " + bikeID + " with new value " + newValue);
        } 
        catch (SQLException e) {
            System.out.println("We were unable to update the bike. Are you sure the bike exists?");
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }
    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void viewBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Here are all the bikes registered: ");
        String query = "SELECT bike_id, brand, cc, broken FROM bike";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            System.out.println("Query failed. Try again.");
        }

        // Print the resultset
        Utils.printSet(rs);
    }

    
    /** 
     * @param rs
     * @param stmt
     * @param conn
     * @param scanner
     */
    private void createBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Enter the bike's brand between 1-30 characters: ");
        String brand = scanner.next();

        System.out.println("Please enter a number for bike type: ");
        int bikeType = scanner.nextInt();

        System.out.println("Please enter the license plate between 1-30 characters: ");
        String licensePlate = scanner.next();

        System.out.println("Please enter the bike's VIN between 1-30 characters: ");
        String vin = scanner.next();

        System.out.println("Please enter a number to specify if the bike is broken: ");
        int broken = scanner.nextInt();

        System.out.println("Please enter a number to specify the bike's CCs: ");
        int cc = scanner.nextInt();

        // Build the query and use PreparedStatements to insert the data
        String query = "INSERT INTO bike (brand, type, license_plate, vin, broken, cc) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, brand);
            ps.setInt(2, bikeType);
            ps.setString(3, licensePlate);
            ps.setString(4, vin);
            ps.setInt(5, broken);
            ps.setInt(6, cc);
            ps.executeUpdate();
            System.out.println("Bike created successfully!");
        } catch (SQLException e) {
            System.out.println("Query failed.  Please ensure you entered valid data.");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close prepared statement, leaking resources.");
                }
            }
        }
    }
}