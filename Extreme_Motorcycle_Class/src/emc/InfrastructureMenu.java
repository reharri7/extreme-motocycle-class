package emc;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InfrastructureMenu {
    private static final String SELECT_FROM_BIKE_RANGE = "SELECT * FROM bike_range";
    private static final String SELECT_FROM_BIKE_RANGE_BY_ID = "SELECT * FROM bike_range WHERE range_id = ?";
    private static final String SELECT_RANGE_TYPE_BY_ID = "SELECT * FROM range_type WHERE range_type.range_type_id = ?";
    private static final String INSERT_BIKE_RANGE = "INSERT INTO bike_range (range_type, capacity) VALUES (?, ?)";
    /**
     * Print student menu options and call appropriate method.
     * @param resultSet
     * @param preparedStatement
     * @param connection
     * @param scanner
     */
    public void menu(
            ResultSet resultSet,
            PreparedStatement preparedStatement,
            Connection connection,
            Scanner scanner
    ) {
        int selection;

        while(true) {
            printMenu();
            try {
                selection = scanner.nextInt();
                switch (selection) {
                    case 0 -> {
                        return;
                    }
                    case 1 -> manageRanges(resultSet, preparedStatement, connection, scanner);
                    case 2 -> manageClassrooms(resultSet, preparedStatement, connection, scanner);
                    default -> System.out.println("Invalid selection. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid selection. Please try again.");
                scanner.nextLine();
            }
        }
    }


    private void manageRanges(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection, Scanner scanner) {
        int manageRangeSelection;

        while(true) {
            printRangeMenu();
            try {
                manageRangeSelection = scanner.nextInt();
                switch (manageRangeSelection) {
                    case 0 -> {
                        return;
                    }
                    case 1 -> {
                        createRange(preparedStatement, connection, scanner);
                        viewRanges(connection);
                    }
                    case 2 -> viewRanges(connection);
                    case 3 -> {
                        editRange(preparedStatement, connection, scanner);
                        viewRanges(connection);
                    }
                    case 4 -> {
                        deleteRange(resultSet, preparedStatement, connection, scanner);
                        viewRanges(connection);
                    }
                    case 5 -> assignRange(resultSet, preparedStatement, connection, scanner);
                    case 6 -> unassignRange(resultSet, preparedStatement, connection, scanner);
                    case 7 -> viewRangeSchedule(resultSet, preparedStatement, connection, scanner);
                    case 8 -> createRangeType(resultSet, preparedStatement, connection, scanner);
                    case 9 -> viewRangeTypes(connection);
                    case 10 -> editRangeType(resultSet, preparedStatement, connection, scanner);
                    case 11 -> deleteRangeType(resultSet, preparedStatement, connection, scanner);
                    default -> System.out.println("Invalid selection. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid selection. Please try again.");
                scanner.nextLine();
            }
        }
    }
    private void createRange(PreparedStatement preparedStatement,
                             Connection connection,
                             Scanner scanner
    ) {
        int rangeTypeId;
        int rangeCapacity = 15;

        // Grab all range types
        viewRangeTypes(connection);
        System.out.println("Enter range type ID: ");
        rangeTypeId = scanner.nextInt();

        // verify that range type exists
        if(!rangeTypeExists(preparedStatement, connection, rangeTypeId)) {
            System.out.println("Range type does not exist.");
            return;
        }

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(INSERT_BIKE_RANGE);
            preparedStatement.setInt(1, rangeTypeId);
            preparedStatement.setInt(2, rangeCapacity);
            int createRangeResult = preparedStatement.executeUpdate();
            if(createRangeResult == 1) {
                System.out.println("Range created successfully.");
                connection.commit();
            } else {
                System.out.println("Range creation failed.");
                connection.rollback();
            }
            System.out.println("Range created successfully.");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }
    private void viewRanges(Connection connection) {
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SELECT_FROM_BIKE_RANGE);
            // Print the header
            System.out.println("Bike Ranges");
            System.out.printf("%-5s%-30s%-30s%n", "Range ID", "Range Type", "Capacity");
            while(resultSet.next()) {
                System.out.printf("%-5s%-30s%-30s%n",
                        resultSet.getInt("range_id"),
                        resultSet.getString("range_type"),
                        resultSet.getInt("capacity")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error during range type retrieval.");
        } finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
                if(statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources.");
            }
        }
    }
    private void editRange(PreparedStatement preparedStatement,
                           Connection connection,
                            Scanner scanner
    ) {
        viewRanges(connection);
        System.out.println("Enter range ID to edit: ");
        int rangeId = scanner.nextInt();
        // TODO: verify range exists

        int rangeTypeId = scanner.nextInt();
        // verify bike range type exists
        if(!rangeTypeExists(preparedStatement, connection, rangeTypeId)) {
            System.out.println("Range type does not exist.");
            return;
        }

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("UPDATE bike_range SET range_type = ? WHERE range_id = ?");
            preparedStatement.setInt(1, rangeTypeId);
            int createRangeResult = preparedStatement.executeUpdate();
            if(createRangeResult == 1) {
                System.out.println("Range updated successfully.");
                connection.commit();
            } else {
                System.out.println("Range update failed.");
                connection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Error during range edit.");
        }

    }
    private void deleteRange(ResultSet resultSet,
                             PreparedStatement preparedStatement,
                             Connection connection,
                             Scanner scanner
    ) {
        throw new RuntimeException("Not implemented yet.");

    }
    private void viewRangeSchedule(ResultSet resultSet,
                                   PreparedStatement preparedStatement,
                                   Connection connection,
                                   Scanner scanner
    ) {
        throw new RuntimeException("Not implemented yet.");
    }
    private void assignRange(ResultSet resultSet,
                             PreparedStatement preparedStatement,
                             Connection connection,
                             Scanner scanner
    ) {
        throw new RuntimeException("Not implemented yet.");
    }
    private void unassignRange(ResultSet resultSet,
                               PreparedStatement preparedStatement,
                               Connection connection,
                               Scanner scanner
    ) {
        throw new RuntimeException("Not implemented yet.");
    }
    private void createRangeType(ResultSet resultSet,
                                 PreparedStatement preparedStatement,
                                 Connection connection,
                                 Scanner scanner
    ) {
        throw new RuntimeException("Not implemented yet.");
    }

    private void viewRangeTypes(Connection connection) {
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM range_type");
            // Print the header
            System.out.printf("%-5s%-30s%n", "ID", "Range Type");
            while(resultSet.next()) {
                System.out.printf("%-5s%-30s%n",
                        resultSet.getInt("range_type_id"),
                        resultSet.getString("range_type_value")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error during range type retrieval.");
        } finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
                if(statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources.");
            }
        }
    }
    private boolean rangeExists(PreparedStatement preparedStatement, Connection connection, int rangeId) {
        ResultSet resultSet = null;
        boolean rangeExists = false;
        try {
            preparedStatement = connection.prepareStatement(SELECT_FROM_BIKE_RANGE_BY_ID);
            preparedStatement.setInt(1, rangeId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                rangeExists = true;
            }
        } catch (SQLException e) {
            System.out.println("Error during range retrieval.");
        } finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
                if(preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources.");
            }
        }
        return rangeExists;
    }
    private boolean rangeTypeExists(
                               PreparedStatement preparedStatement,
                               Connection connection,
                               int rangeTypeID
    ) {
        ResultSet resultSet = null;
        boolean rangeTypeExists = false;
        try {
            preparedStatement = connection.prepareStatement(SELECT_RANGE_TYPE_BY_ID);
            preparedStatement.setInt(1, rangeTypeID);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                rangeTypeExists = true;
            }
        } catch (SQLException e) {
            System.out.println("Error during range type retrieval.");
        } finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
                if(preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources.");
            }
        }
        return rangeTypeExists;
    }
    private void editRangeType(ResultSet resultSet,
                               PreparedStatement preparedStatement,
                               Connection connection,
                               Scanner scanner
    ) {
        throw new RuntimeException("Not implemented yet.");
    }
    private void deleteRangeType(ResultSet resultSet,
                                 PreparedStatement preparedStatement,
                                 Connection connection,
                                 Scanner scanner
    ) {
        throw new RuntimeException("Not implemented yet.");
    }
    private void printRangeMenu() {
        System.out.println("Range Menu");
        System.out.println("1. Create Range");
        System.out.println("2. View Ranges");
        System.out.println("3. Edit Range");
        System.out.println("4. Delete Range");
        System.out.println("5. Assign Range");
        System.out.println("6. Unassign Range");
        System.out.println("7. View Range Schedule");
        System.out.println("8. Create Range Type");
        System.out.println("9. View Range Types");
        System.out.println("10. Edit Range Types");
        System.out.println("11. Delete Range Types");
        System.out.println("0. Infrastructure Menu");
    }

    private void manageClassrooms(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection, Scanner scanner) {
        throw new RuntimeException("Not implemented yet.");
    }
    /**
     * Prints main infrastructure menu to console.
     */
    private void printMenu() {
        System.out.println("Infrastructure Menu");
        System.out.println("1. Manage Ranges");
        System.out.println("2. Manage Classrooms");
        System.out.println("0. Main Menu");
    }
}
