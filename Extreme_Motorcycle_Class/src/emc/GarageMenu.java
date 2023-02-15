package emc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GarageMenu {

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
                System.out.println("Please enter an integer value between 0 and (TODO:)");
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

    private void deleteBikeType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike type's ID: ");
        int bikeTypeID = scanner.nextInt();

        String query = "DELETE FROM bike_type WHERE bike_type_id = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, bikeTypeID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void editBikeType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike type's ID: ");
        int bikeTypeID = scanner.nextInt();

        System.out.println("Please enter the attribute you wish to change: ");
        String attribute = scanner.next();

        System.out.println("Please enter the new value for the attribute: ");
        String newValue = scanner.next();

        /* Make a switch case to determine whether to cast the input to another type */
    }

    private void viewBikeTypes(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Here are all the bike types registered:");

    }

    private void createBikeType(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a name for the bike type between 1 and 10 characters: ");
        String bikeTypeName = scanner.next();
    }

    private void deleteBikeProblem(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the problem ID: ");
        int problemID = scanner.nextInt();

        String query = "DELETE FROM bike_problem WHERE problem_id = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, problemID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void editBikeProblem(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike's ID: ");
        int bikeID = scanner.nextInt();

        System.out.println("Please enter the attribute you wish to change: ");
        String attribute = scanner.next();

        System.out.println("Please enter the new value for the attribute: ");
        String newValue = scanner.next();

        /* Make a switch case to determine whether to cast the input to another type */

    }

    private void viewBikeProblem(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike's ID: ");
        int bikeID = scanner.nextInt();

    }

    private void createBikeProblem(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike's ID: ");
        int bikeID = scanner.nextInt();

        System.out.println("Please enter a date the problem occured: ");
        String date = scanner.next();

        System.out.println("Please enter a description of the problem between 1 and 150 characters: ");
        String description = scanner.next();

        System.out.println("Please enter a date to specify the repair date: ");
        String repairDate = scanner.next();

        System.out.println("Please enter a number for the cost of repair: ");
        int cost = scanner.nextInt();

        String query = "INSERT INTO bike_problem (problem_date, bike_id, repair_date, description, cost) VALUES (?, ?, ?, ?, ?)";
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
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void bikeAssignmentReport(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bikes ID: ");
        int bikeID = scanner.nextInt();

    }

    private void bikeProblemReport(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike's ID: ");
        int bikeID = scanner.nextInt();

    }

    private void unassignBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike assignment ID: ");
        int bikeAssignmentID = scanner.nextInt();

        PreparedStatement ps = null;
        String query = "DELETE FROM bike_assignment WHERE bike_assignment_id = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, bikeAssignmentID);
            ps.executeUpdate();
            System.out.println("Sucessfully unassigned bike " + bikeAssignmentID);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

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
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void deleteBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike's ID you wish to delete: ");
        int bikeID = scanner.nextInt();

        String query = "DELETE FROM bike WHERE bikeID = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, bikeID);
            System.out.println("Sucessfully deleted bike: " + bikeID);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void editBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Please enter a number for the bike's ID you wish to edit: ");
        int bikeID = scanner.nextInt();

        System.out.println("Please enter the attribute you wish to edit: ");
        String attribute = scanner.next();

        System.out.println("Please enter the new value for the attribute: ");
        String newValue = scanner.next();

        /* Make a switch case to determine whether to cast the input to another type */

    }

    private void viewBike(ResultSet rs, Statement stmt, Connection conn, Scanner scanner) {
        System.out.println("Here are all the bikes registered: ");
        String query = "SELECT * FROM bike";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Print the resultset
        try {
            while (rs.next()) {

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

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
        String query = "INSERT INTO bike (brand, bike_type, license_plate, vin, broken, cc) VALUES (?, ?, ?, ?, ?, ?)";
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
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
