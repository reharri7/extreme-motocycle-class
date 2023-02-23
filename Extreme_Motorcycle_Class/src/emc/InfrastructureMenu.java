package emc;

import java.sql.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class InfrastructureMenu {
    private static final String SELECT_FROM_BIKE_RANGE = "SELECT * FROM bike_range";
    private static final String SELECT_FROM_BIKE_RANGE_BY_ID = "SELECT * FROM bike_range WHERE range_id = ?";
    private static final String SELECT_RANGE_TYPE_BY_ID = "SELECT * FROM range_type WHERE range_type.range_type_id = ?";
    private static final String INSERT_BIKE_RANGE = "INSERT INTO bike_range (range_type, capacity) VALUES (?, ?)";
    private static final String UPDATE_BIKE_RANGE_BY_ID = "UPDATE bike_range SET range_type = ? WHERE range_id = ?";
    private static final String DELETE_BIKE_RANGE_BY_ID = "DELETE from bike_range WHERE range_id = ?";
    private static final String INSERT_RANGE_ASSIGNMENT = "insert into range_assignment (range_id, course_schedule_id) values (?, ?)";
    private static final String SELECT_ALL_COURSE = "SELECT * FROM course";
    private static final String SELECT_RANGE_AVAILABILITY_BY_WEEK = "SELECT ra.range_id " +
            " from course_schedule cs" +
            " left join range_assignment ra on cs.course_schedule_id = ra.course_schedule_id " +
            " where ra.range_id = ? " +
            " and cs.course_date = ? " +
            " and cs.time_type_id = ?";
    private static final String SELECT_COURSE_SCHEDULE_BY_WEEK_WITHOUT_RANGE_ASSIGNMENT =
            "SELECT cs.course_schedule_id, " +
                    " cs.course_date, " +
                    " course.course_name, " +
                    " ct.course_type_value, " +
                    " tt.time_type_value " +
                    " FROM course " +
                    " left join course_schedule cs on course.course_id = cs.course_id " +
                    " left join course_type ct on course.course_type = ct.course_type_id " +
                    " left join time_type tt on cs.time_type_id = tt.time_type_id " +
                    " WHERE course.course_id = cs.course_id " +
                    " and cs.course_schedule_id not in (select distinct ra.course_schedule_id from range_assignment ra)" +
                    " AND cs.course_date >= ? " +
                    " AND cs.course_date < ? " +
                    " ORDER BY cs.course_date";
    private static final String SELECT_AVAILABLE_RANGE_BY_COURSE_SCHEDULE_ID = "" +
            "select bike_range.range_id, " +
            "rt.range_type_value, " +
            "bike_range.capacity " +
            "from bike_range " +
            "left join range_type rt on bike_range.range_type = rt.range_type_id " +
            "where bike_range.range_id not in " +
            "(select distinct ra.range_id from course_schedule " +
            "left join range_assignment ra on course_schedule.course_schedule_id = ra.course_schedule_id " +
            "where course_schedule.course_schedule_id = ?) " +
            "or (select distinct ra.range_id from course_schedule " +
            "left join range_assignment ra on course_schedule.course_schedule_id = ra.course_schedule_id " +
            "where course_schedule.course_schedule_id = ?) is null";
    private static final String SELECT_AVAILABLE_RANGES_BY_DATE_AND_TIME = "select range_id " +
            "from bike_range " +
            "where bike_range.range_id not in (select ra.range_id " +
            "from range_assignment ra " +
            "left join course_schedule cs on ra.course_schedule_id = cs.course_schedule_id " +
            "where cs.course_date = ? and cs.time_type_id = ?) " +
            "order by range_id";
    private static final String DELETE_RANGE_ASSIGNMENT_BY_ID = "delete from range_assignment where range_assignment_id = ?";
    private static final String INSERT_CLASSROOM = "INSERT INTO classroom (capacity) VALUES (?)";
    private static final String UPDATE_CLASSROOM_BY_ID = "UPDATE classroom SET capacity = ? WHERE classroom_id = ?";
    private static final String SELECT_ALL_CLASSROOMS = "SELECT * FROM classroom";
    private static final String DELETE_CLASSROOM_BY_ID = "DELETE FROM classroom WHERE classroom_id = ?";
    private static final String SELECT_CLASSROOM_SCHEDULE = "SELECT cs.course_schedule_id, " +
            "cs.course_date, " +
            "course.course_name, " +
            "ct.course_type_value, " +
            "tt.time_type_value, " +
            "ca.classroom_assignment_id, " +
            "ca.classroom_id " +
            "FROM course " +
            "left join course_schedule cs on course.course_id = cs.course_id " +
            "left join course_type ct on course.course_type = ct.course_type_id " +
            "left join time_type tt on cs.time_type_id = tt.time_type_id " +
            "left join classroom_assignment ca on cs.course_schedule_id = ca.course_schedule_id " +
            "WHERE course.course_id = cs.course_id " +
            "AND cs.course_date >= ? " +
            "AND cs.course_date < ? " +
            "ORDER BY cs.course_date";
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
                    case 5 -> assignRange(connection, scanner);
                    case 6 -> unassignRange(connection, scanner);
                    case 7 -> viewRangeSchedule(resultSet, preparedStatement, connection, scanner);
                    case 8 -> createRangeType(connection, scanner);
                    case 9 -> viewRangeTypes(connection);
                    case 10 -> editRangeType(resultSet, preparedStatement, connection, scanner);
                    case 11 -> deleteRangeType(preparedStatement, connection, scanner);
                    default -> System.out.println("Invalid selection. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid selection. Please try again.");
                scanner.nextLine();
            }
        }
    }
    private void manageClassrooms(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection, Scanner scanner) {
        int manageClassroomSelection;

        while(true) {
            printRangeMenu();
            try {
                manageClassroomSelection = scanner.nextInt();
                switch (manageClassroomSelection) {
                    case 0 -> {
                        return;
                    }
                    case 1 -> {
                        createClassroom(preparedStatement, connection, scanner);
                        viewClassrooms(connection);
                    }
                    case 2 -> viewClassrooms(connection);
                    case 3 -> {
                        editClassroom(preparedStatement, connection, scanner);
                        viewClassrooms(connection);
                    }
                    case 4 -> {
                        deleteClassroom(resultSet, preparedStatement, connection, scanner);
                        viewClassrooms(connection);
                    }
                    case 5 -> assignClassroom(connection, scanner);
                    case 6 -> unassignClassroom(connection, scanner);
                    case 7 -> viewClassroomSchedule(resultSet, preparedStatement, connection, scanner);
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid selection. Please try again.");
                scanner.nextLine();
            }
        }
    }

    private void createClassroom(PreparedStatement preparedStatement, Connection connection, Scanner scanner) {
        ResultSet resultSet = null;
        System.out.println("Enter classroom capacity: ");
        int capacity = scanner.nextInt();
        try {
            preparedStatement = connection.prepareStatement(INSERT_CLASSROOM);
            preparedStatement.setInt(1, capacity);
            resultSet = preparedStatement.executeQuery(INSERT_CLASSROOM);
            // Print the header
            System.out.println("Classrooms");
            Utils.printSet(resultSet);
        } catch (SQLException e) {
            System.out.println("Error during classroom insert.");
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
    }

    private void editClassroom(PreparedStatement preparedStatement, Connection connection, Scanner scanner) {
        ResultSet resultSet = null;
        System.out.println("Enter classroom id: ");
        int classroomId = scanner.nextInt();
        System.out.println("Enter classroom capacity: ");
        int capacity = scanner.nextInt();
        try {
            preparedStatement = connection.prepareStatement(UPDATE_CLASSROOM_BY_ID);
            preparedStatement.setInt(1, capacity);
            resultSet = preparedStatement.executeQuery(UPDATE_CLASSROOM_BY_ID);
            // Print the header
            System.out.println("Classrooms");
            Utils.printSet(resultSet);
        } catch (SQLException e) {
            System.out.println("Error during classroom insert.");
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
    }

    private void deleteClassroom(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection, Scanner scanner) {
        System.out.println("Enter classroom id: ");
        int classroomId = scanner.nextInt();
        try {
            preparedStatement = connection.prepareStatement(DELETE_CLASSROOM_BY_ID);
            preparedStatement.setInt(1, classroomId);
            resultSet = preparedStatement.executeQuery(DELETE_CLASSROOM_BY_ID);
            // Print the header
            System.out.println("Classrooms");
            Utils.printSet(resultSet);
        } catch (SQLException e) {
            System.out.println("Error during classroom delete.");
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
    }

    private void viewClassrooms(Connection connection) {
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(SELECT_ALL_CLASSROOMS);
            // Print the header
            System.out.println("Classrooms");
            Utils.printSet(resultSet);
        } catch (SQLException e) {
            System.out.println("Error during classroom select.");
        } finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources.");
            }
        }

    }

    private void assignClassroom(Connection connection, Scanner scanner) {
        throw new RuntimeException("Not implemented yet.");

    }

    private void unassignClassroom(Connection connection, Scanner scanner) {
        throw new RuntimeException("Not implemented yet.");

    }

    private void viewClassroomSchedule(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection, Scanner scanner) {
        throw new RuntimeException("Not implemented yet.");
//        System.out.println("Enter classroom id: ");
//        int classroomId = scanner.nextInt();
//        try {
//            preparedStatement = connection.prepareStatement(SELECT_CLASSROOM_SCHEDULE);
//            preparedStatement.setInt(1, classroomId);
//            resultSet = preparedStatement.executeQuery(SELECT_CLASSROOM_SCHEDULE);
//            // Print the header
//            System.out.println("Classrooms");
//            Utils.printSet(resultSet);
//        } catch (SQLException e) {
//            System.out.println("Error during classroom select.");
//        } finally {
//            try {
//                if(resultSet != null) {
//                    resultSet.close();
//                }
//                if(preparedStatement != null) {
//                    preparedStatement.close();
//                }
//            } catch (SQLException e) {
//                System.out.println("Error closing resources.");
//            }
//        }
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
        // verify range exists
        if(!rangeExists(preparedStatement, connection, rangeId)) {
            System.out.println("Range does not exist.");
            return;
        }
        // TODO Print range type options
        System.out.println("Enter new range type ID: ");
        int rangeTypeId = scanner.nextInt();
        // verify bike range type exists
        if(!rangeTypeExists(preparedStatement, connection, rangeTypeId)) {
            System.out.println("Range type does not exist.");
            return;
        }

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(UPDATE_BIKE_RANGE_BY_ID);
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
        viewRanges(connection);
        System.out.println("Enter range ID to delete: ");
        int rangeId = scanner.nextInt();
        // verify range exists
        if(!rangeExists(preparedStatement, connection, rangeId)) {
            System.out.println("Range does not exist.");
            return;
        }

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(DELETE_BIKE_RANGE_BY_ID);
            preparedStatement.setInt(1, rangeId);
            int deleteRangeResult = preparedStatement.executeUpdate();
            if(deleteRangeResult == 1) {
                System.out.println("Range updated successfully.");
                connection.commit();
            } else {
                System.out.println("Range update failed.");
                connection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Error during range edit.");
            e.printStackTrace();
        }
    }
    private void viewRangeSchedule(ResultSet resultSet,
                                   PreparedStatement preparedStatement,
                                   Connection connection,
                                   Scanner scanner
    ) {
        int viewRangeScheduleSelection;

        while(true) {
            printViewRangeScheduleSubMenu();
            try {
                viewRangeScheduleSelection = scanner.nextInt();
                switch (viewRangeScheduleSelection) {
                    case 0 -> {
                        return;
                    }
                    case 1 -> viewWeeklyScheduleOfRange(connection, scanner);
                    case 2 -> viewRangeAvailability(connection, scanner);

                    default -> System.out.println("Invalid selection. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid selection. Please try again.");
                scanner.nextLine();
            }
        }
    }

    private void viewRangeAvailability(Connection connection, Scanner scanner) {
        System.out.println("Enter year for which would like to view range availability: ");
        int year = scanner.nextInt();
        System.out.println("Enter month for which would like to view range availability: ");
        int month = scanner.nextInt();
        System.out.println("Enter day for which would like to view range availability: ");
        int day = scanner.nextInt();
        LocalDate date;
        System.out.println("Would you like to view availability for morning or afternoon?");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        int timeOfDay = scanner.nextInt();
        if(timeOfDay != 1 && timeOfDay != 2) {
            System.out.println("Invalid selection.");
            return;
        }
        try {
            date = Utils.createLocalDate(year, month, day);
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AVAILABLE_RANGES_BY_DATE_AND_TIME);
            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.setInt(2, timeOfDay);
            ResultSet resultSet = preparedStatement.executeQuery();
            Utils.printSet(resultSet);
        } catch (DateTimeException dateTimeException) {
            System.out.println("Invalid date entered.");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewWeeklyScheduleOfRange(Connection connection, Scanner scanner) {
        viewRanges(connection);
        System.out.println("Which range would you like to view the schedule for?");
        System.out.println("Enter range ID: ");
        int rangeId = scanner.nextInt();
        System.out.println("Enter year for which would like to view range schedule: ");
        int year = scanner.nextInt();
        System.out.println("Enter month for which would like to view range schedule: ");
        int month = scanner.nextInt();
        System.out.println("Enter day for which would like to view range schedule: ");
        int day = scanner.nextInt();
        LocalDate date;
        try {
            date = Utils.createLocalDate(year, month, day);
        } catch (DateTimeException dateTimeException) {
            System.out.println("Invalid date entered.");
            return;
        }
        System.out.println("Available times for range " + rangeId + " for week of " + date + ":");
        for(int i = 0; i < 7; i++) {
            LocalDate currentDate = date.plusDays(i);
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_RANGE_AVAILABILITY_BY_WEEK);
                preparedStatement.setInt(1, rangeId);
                preparedStatement.setDate(2, Date.valueOf(currentDate));
                preparedStatement.setInt(3, 1);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    continue;
                }
                System.out.println(currentDate + " AM");
                preparedStatement = connection.prepareStatement(SELECT_RANGE_AVAILABILITY_BY_WEEK);
                preparedStatement.setInt(1, rangeId);
                preparedStatement.setDate(2, Date.valueOf(currentDate));
                preparedStatement.setInt(3, 2);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    continue;
                }
                    System.out.println(currentDate + " PM");
            } catch (SQLException e) {
                System.out.println("Error during range schedule retrieval.");
                e.printStackTrace();
            }
        }
    }

    private void assignRange(Connection connection, Scanner scanner) {
        System.out.println("Enter year for which would like to view course schedule: ");
        int year = scanner.nextInt();
        System.out.println("Enter month for which would like to view course schedule: ");
        int month = scanner.nextInt();
        System.out.println("Enter day for which would like to view course schedule: ");
        int day = scanner.nextInt();
        LocalDate date;
        try {
            date = Utils.createLocalDate(year, month, day);
        } catch (DateTimeException dateTimeException) {
            System.out.println("Invalid date entered.");
            return;
        }
        LocalDate nextWeek = date.plusDays(7);

        try {
            // Get the course schedule
            PreparedStatement courseSchedulePreparedStatement = connection.prepareStatement(SELECT_COURSE_SCHEDULE_BY_WEEK_WITHOUT_RANGE_ASSIGNMENT);
            courseSchedulePreparedStatement.setDate(1, Date.valueOf(date));
            courseSchedulePreparedStatement.setDate(2, Date.valueOf(nextWeek));
            ResultSet courseScheduleResultSet = courseSchedulePreparedStatement.executeQuery();
            // Print the header
            System.out.println("Course Schedule");
            System.out.printf("%-30s%-30s%-30s%-30s%n",
                    "Course Schedule ID",
                    "Course Date",
                    "Course Name",
                    "Course Type"
            );
            while(courseScheduleResultSet.next()) {
                System.out.printf("%-30s%-30s%-30s%-30s%n",
                        courseScheduleResultSet.getInt("course_schedule_id"),
                        courseScheduleResultSet.getDate("course_date"),
                        courseScheduleResultSet.getString("course_name"),
                        courseScheduleResultSet.getString("course_type_value")
                );
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Enter course schedule ID for which you would like to assign a range: ");
        int courseScheduleId = scanner.nextInt();
        List<Integer> listOfAvailableRanges = new ArrayList<>();
        try {
            // Find the ranges available for the given date
            PreparedStatement availableRangesPreparedStatement = connection.prepareStatement(SELECT_AVAILABLE_RANGE_BY_COURSE_SCHEDULE_ID);
            availableRangesPreparedStatement.setInt(1, courseScheduleId);
            availableRangesPreparedStatement.setInt(2, courseScheduleId);
            ResultSet availableRangesResultSet = availableRangesPreparedStatement.executeQuery();
            // Print the header
            System.out.println("Available Ranges");
            System.out.printf("%-10s%-30s%-30s%n",
                    "Range ID",
                    "Range Type",
                    "Capacity"
            );
            while(availableRangesResultSet.next()) {
                listOfAvailableRanges.add(availableRangesResultSet.getInt("range_id"));
                System.out.printf("%-10s%-30s%-30s%n",
                        availableRangesResultSet.getInt("range_id"),
                        availableRangesResultSet.getString("range_type_value"),
                        availableRangesResultSet.getInt("capacity")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Enter range ID to assign: ");
        int rangeId = scanner.nextInt();

        // verify range is available and exists
        if(!listOfAvailableRanges.contains(rangeId)) {
            System.out.println("Range is not available or does not exist.");
            return;
        }
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_RANGE_ASSIGNMENT);
            preparedStatement.setInt(1, rangeId);
            preparedStatement.setInt(2, courseScheduleId);
            int createRangeAssignmentResult = preparedStatement.executeUpdate();
            if(createRangeAssignmentResult == 1) {
                System.out.println("Range assignment created successfully.");
                connection.commit();
            } else {
                System.out.println("Range assignment creation failed.");
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private void unassignRange(Connection connection,
                               Scanner scanner
    ) {
        printRangeAssignments(connection);
        System.out.println("Enter range assignment ID to unassign: ");
        int rangeAssignmentId = scanner.nextInt();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_RANGE_ASSIGNMENT_BY_ID);
            preparedStatement.setInt(1, rangeAssignmentId);
            int deleteRangeAssignmentResult = preparedStatement.executeUpdate();
            if(deleteRangeAssignmentResult == 1) {
                System.out.println("Range assignment deleted successfully.");
                connection.commit();
            } else {
                System.out.println("Range assignment deletion failed.");
                connection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Error during range assignment deletion.");
        }
    }
    private void printRangeAssignments(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM range_assignment");
            System.out.println("Range Assignments");
            Utils.printSet(resultSet);
        } catch (SQLException e) {
            System.out.println("Error during range assignment retrieval.");
        }
    }
    private void createRangeType(Connection connection,
                                 Scanner scanner
    ) {
        String rangeType;
        System.out.println("Enter range type: ");
        rangeType = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO range_type (range_type_value) VALUES (?)");
            preparedStatement.setString(1, rangeType);
            int createRangeTypeResult = preparedStatement.executeUpdate();
            if(createRangeTypeResult == 1) {
                System.out.println("Range type created successfully.");
                connection.commit();
                viewRangeTypes(connection);
            } else {
                System.out.println("Range type creation failed.");
                connection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Error during range type creation.");
        }
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
        } catch (SQLException e) {
            System.out.println("Error during range retrieval.");
        }
        try {
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
        viewRangeTypes(connection);
        System.out.println("Enter range type ID to edit: ");
        int rangeTypeId = scanner.nextInt();
        if(!rangeTypeExists(preparedStatement, connection, rangeTypeId)) {
            System.out.println("Range type does not exist.");
            return;
        }
        String rangeType;
        System.out.println("Enter new range type: ");
        rangeType = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("UPDATE range_type SET range_type_value = ? WHERE range_type_id = ?");
            preparedStatement.setString(1, rangeType);
            preparedStatement.setInt(2, rangeTypeId);
            int editRangeTypeResult = preparedStatement.executeUpdate();
            if(editRangeTypeResult == 1) {
                System.out.println("Range type edited successfully.");
                connection.commit();
            } else {
                System.out.println("Range type edit failed.");
                connection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Error during range type edit.");
        }
    }
    private void deleteRangeType(PreparedStatement preparedStatement,
                                 Connection connection,
                                 Scanner scanner
    ) {
        viewRangeTypes(connection);
        System.out.println("Enter range type ID to delete: ");
        int rangeTypeId = scanner.nextInt();
        if(!rangeTypeExists(preparedStatement, connection, rangeTypeId)) {
            System.out.println("Range type does not exist.");
            return;
        }
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("DELETE FROM range_type WHERE range_type_id = ?");
            preparedStatement.setInt(1, rangeTypeId);
            int deleteRangeTypeResult = preparedStatement.executeUpdate();
            if(deleteRangeTypeResult == 1) {
                System.out.println("Range type deleted successfully.");
                connection.commit();
            } else {
                System.out.println("Range type deletion failed.");
                connection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Error during range type deletion.");
        }
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


    /**
     * Prints main infrastructure menu to console.
     */
    private void printMenu() {
        System.out.println("Infrastructure Menu");
        System.out.println("1. Manage Ranges");
        System.out.println("2. Manage Classrooms");
        System.out.println("0. Main Menu");
    }

    private void printViewRangeScheduleSubMenu() {
        System.out.println("View Range Sub Menu");
        System.out.println("1. Weekly Schedule of Ranges");
        System.out.println("2. View Available Ranges");
        System.out.println("0. Manage Ranges Menu");
    }
}
