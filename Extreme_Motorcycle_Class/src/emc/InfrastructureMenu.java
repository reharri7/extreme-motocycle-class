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
            " select bike_range.range_id, " +
            " rt.range_type_value, " +
            " bike_range.capacity " +
            " from bike_range " +
            " left join range_type rt on bike_range.range_type = rt.range_type_id " +
            " where bike_range.range_id not in " +
            " (select distinct ra.range_id from course_schedule " +
            " left join range_assignment ra on course_schedule.course_schedule_id = ra.course_schedule_id " +
            " where course_schedule.course_schedule_id = ? " +
            " and course_schedule.course_date = ?) " +
            " or (select distinct ra.range_id from course_schedule " +
            " left join range_assignment ra on course_schedule.course_schedule_id = ra.course_schedule_id " +
            " where course_schedule.course_schedule_id = ? " +
            " and course_schedule.course_date = ?) is null";
    private static final String SELECT_AVAILABLE_RANGES_BY_DATE_AND_TIME = "select range_id " +
            "from bike_range " +
            "where bike_range.range_id not in (select ra.range_id " +
            "from range_assignment ra " +
            "left join course_schedule cs on ra.course_schedule_id = cs.course_schedule_id " +
            "where cs.course_date = ? and cs.time_type_id = ?) " +
            "order by range_id";
    private static final String SELECT_AVAILABLE_CLASSROOMS_BY_DATE_AND_TIME = "select classroom_id " +
            "from classroom " +
            "where classroom_id not in (select cs.classroom_id " +
            "from course_schedule cs " +
            "where cs.course_date = ? and cs.time_type_id = ?) " +
            "order by classroom_id";
    private static final String DELETE_RANGE_ASSIGNMENT_BY_ID = "delete from range_assignment where range_assignment_id = ?";
    private static final String INSERT_CLASSROOM = "INSERT INTO classroom (capacity) VALUES (15)";
    private static final String SELECT_ALL_CLASSROOMS = "SELECT * FROM classroom";
    private static final String DELETE_CLASSROOM_BY_ID = "DELETE FROM classroom WHERE classroom_id = ?";
    private static final String INSERT_COURSE_SCHEDULE = "insert into course_schedule (course_id, course_date, time_type_id, classroom_id) values(?, ?, ?, ?)";
    private static final String SELECT_ALL_COURSE_SCHEDULES = "SELECT * FROM course_schedule";
    private static final String SELECT_COURSE_SCHEDULE_DATE_BY_ID = "SELECT course_date FROM course_schedule WHERE course_schedule_id = ?";
    private static final String DELETE_COURSE_SCHEDULE_BY_ID = "DELETE FROM course_schedule WHERE course_schedule_id = ?";
    private static final String UPDATE_COURSE_SCHEDULE_BY_ID = "UPDATE course_schedule SET course_id = ?, course_date = ?, time_type_id = ?, classroom_id = ? WHERE course_schedule_id = ?";
    private static final String SELECT_CLASSROOM_SCHEDULE = "SELECT cs.course_schedule_id " +
            " from course_schedule cs " +
            " left join classroom cl on cs.classroom_id = cl.classroom_id " +
            " where cs.classroom_id = ? " +
            " and cs.course_date = ? " +
            " and cs.time_type_id = ?";
    private static final String SELECT_RANGE_AVAILABILITY_BY_WEEK = "SELECT ra.range_id " +
            " from course_schedule cs" +
            " left join range_assignment ra on cs.course_schedule_id = ra.course_schedule_id " +
            " where ra.range_id = ? " +
            " and cs.course_date = ? " +
            " and cs.time_type_id = ?";
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
                selection = Integer.parseInt(scanner.nextLine());
                switch (selection) {
                    case 0 -> {
                        return;
                    }
                    case 1 -> manageRanges(preparedStatement, connection, scanner);
                    case 2 -> manageClassrooms(preparedStatement, connection, scanner);
                    default -> System.out.println("Invalid selection. Please try again.");
                }
            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Invalid selection. Please try again.");
                scanner.nextLine();
            }
        }
    }


    private void manageRanges(PreparedStatement preparedStatement, Connection connection, Scanner scanner) {
        int manageRangeSelection;

        while(true) {
            printRangeMenu();
            try {
                manageRangeSelection = Integer.parseInt(scanner.nextLine());
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
                        deleteRange(preparedStatement, connection, scanner);
                        viewRanges(connection);
                    }
                    case 5 -> assignRange(connection, scanner);
                    case 6 -> unassignRange(connection, scanner);
                    case 7 -> viewRangeSchedule(connection, scanner);
                    case 8 -> createRangeType(connection, scanner);
                    case 9 -> viewRangeTypes(connection);
                    case 10 -> editRangeType(preparedStatement, connection, scanner);
                    case 11 -> deleteRangeType(preparedStatement, connection, scanner);
                    default -> System.out.println("Invalid selection. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid selection. Please try again.");
                scanner.nextLine();
            }
        }
    }
    private void manageClassrooms(PreparedStatement preparedStatement, Connection connection, Scanner scanner) {
        int manageClassroomSelection;

        while(true) {
            printClassroomMenu();
            try {
                manageClassroomSelection = Integer.parseInt(scanner.nextLine());
                switch (manageClassroomSelection) {
                    case 0 -> {
                        return;
                    }
                    case 1 -> {
                        createClassroom(connection);
                        viewClassrooms(connection);
                    }
                    case 2 -> viewClassrooms(connection);
                    case 3 -> {
                        deleteClassroom(preparedStatement, connection, scanner);
                        viewClassrooms(connection);
                    }
                    case 4 -> viewClassroomSchedule(connection, scanner);
                    case 5 -> {
                        createCourseSchedule(preparedStatement, connection, scanner);
                        viewCourseSchedules(connection);
                    }
                    case 6 -> viewCourseSchedules(connection);
                    case 7 -> {
                        editCourseSchedule(connection, scanner);
                        viewCourseSchedules(connection);
                    }
                    case 8 -> {
                        deleteCourseSchedule(connection, scanner);
                        viewCourseSchedules(connection);
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid selection. Please try again.");
                scanner.nextLine();
            }
        }
    }

    private void printClassroomMenu() {
        System.out.println("Classroom Management Menu");
        System.out.println("1. Create classroom");
        System.out.println("2. View classrooms");
        System.out.println("3. Delete classroom");
        System.out.println("4. View classroom schedule");
        System.out.println("5. Create course schedule");
        System.out.println("6. View course schedule");
        System.out.println("7. Edit course schedule");
        System.out.println("8. Delete course schedule");
        System.out.println("0. Return to main menu");
    }

    private void deleteCourseSchedule(Connection connection, Scanner scanner) {
        viewCourseSchedules(connection);
        System.out.println("Enter course schedule id: ");
        int courseScheduleId = Integer.parseInt(scanner.nextLine());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_COURSE_SCHEDULE_BY_ID);
            preparedStatement.setInt(1, courseScheduleId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editCourseSchedule(Connection connection, Scanner scanner) {
        viewCourseSchedules(connection);
        System.out.println("Enter course schedule id: ");
        int courseScheduleId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter course id: ");
        int courseId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter year for course schedule: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter month for course schedule: ");
        int month = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter day for course schedule: ");
        int day = Integer.parseInt(scanner.nextLine());
        LocalDate date = LocalDate.of(year, month, day);
        System.out.println("Enter classroom id: ");
        int classroomId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter 1 for Morning or 2 for Afternoon: ");
        int timeType = Integer.parseInt(scanner.nextLine());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_COURSE_SCHEDULE_BY_ID);
            preparedStatement.setInt(1, courseId);
            preparedStatement.setDate(2, Date.valueOf(date));
            preparedStatement.setInt(3, timeType);
            preparedStatement.setInt(4, classroomId);
            preparedStatement.setInt(5, courseScheduleId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewCourseSchedules(Connection connection) {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(SELECT_ALL_COURSE_SCHEDULES);
            resultSet = preparedStatement.executeQuery();
            Utils.printSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCourseSchedule(PreparedStatement preparedStatement, Connection connection, Scanner scanner) {
        ResultSet resultSet = null;
        CourseMenu.viewAllCourses(resultSet, preparedStatement, connection);
        System.out.println("Enter course id: ");
        int courseId = Integer.parseInt(scanner.nextLine());
        viewClassrooms(connection);
        System.out.println("Enter classroom id: ");
        int classroomId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter year for course schedule: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter month for course schedule: ");
        int month = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter day for course schedule: ");
        int day = Integer.parseInt(scanner.nextLine());
        LocalDate date = Utils.createLocalDate(year, month, day);
        System.out.println("Enter 1 for Morning or 2 for Afternoon: ");
        int timeType = Integer.parseInt(scanner.nextLine());

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(INSERT_COURSE_SCHEDULE);
            preparedStatement.setInt(1, courseId);
            preparedStatement.setDate(2, Date.valueOf(date));
            preparedStatement.setInt(3, timeType);
            preparedStatement.setInt(4, classroomId);
            int result = preparedStatement.executeUpdate();
            if(result == 1) {
                System.out.println("Course schedule created successfully.");
                connection.commit();
            } else {
                System.out.println("Course schedule creation failed.");
                connection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Error during course schedule insert.");
            e.printStackTrace();
        } finally {
            try {
                if(preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources.");
            }
        }
    }

    private void createClassroom(Connection connection) {
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(INSERT_CLASSROOM);
            int result = preparedStatement.executeUpdate(INSERT_CLASSROOM);
            if(result == 1) {
                System.out.println("Classroom created successfully.");
                connection.commit();
            } else {
                System.out.println("Classroom creation failed.");
                connection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Error during classroom insert.");
            e.printStackTrace();
        } finally {
            try {
                if(preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources.");
            }
        }
    }


    private void deleteClassroom(PreparedStatement preparedStatement, Connection connection, Scanner scanner) {
        System.out.println("Enter classroom id: ");
        int classroomId = Integer.parseInt(scanner.nextLine());
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(DELETE_CLASSROOM_BY_ID);
            preparedStatement.setInt(1, classroomId);
            int result = preparedStatement.executeUpdate();
            if(result == 1) {
                System.out.println("Classroom deleted successfully.");
                connection.commit();
            } else {
                System.out.println("Classroom deletion failed.");
                connection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Error during classroom delete.");
        } finally {
            try {
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

    private void viewClassroomSchedule(Connection connection, Scanner scanner) {
        int viewClassroomScheduleSelection;

        while(true) {
            printViewClassroomScheduleSubMenu();
            try {
                viewClassroomScheduleSelection = Integer.parseInt(scanner.nextLine());
                switch (viewClassroomScheduleSelection) {
                    case 0 -> {
                        return;
                    }
                    case 1 -> {
                        viewWeeklyScheduleOfClassroom(connection, scanner);
                        viewClassrooms(connection);
                    }
                    case 2 -> viewClassroomAvailability(connection, scanner);
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid selection. Please try again.");
                scanner.nextLine();
            }
        }
    }

    private void printViewClassroomScheduleSubMenu() {
        System.out.println("View Classroom Schedule");
        System.out.println("1. View weekly schedule of classroom");
        System.out.println("2. View classroom availability");
        System.out.println("0. Return to previous menu");
    }

    private void viewClassroomAvailability(Connection connection, Scanner scanner) {
        System.out.println("Enter year for which would like to view classroom availability: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter month for which would like to view classroom availability: ");
        int month = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter day for which would like to view classroom availability: ");
        int day = Integer.parseInt(scanner.nextLine());
        LocalDate date;
        System.out.println("Would you like to view availability for morning or afternoon?");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        int timeOfDay = Integer.parseInt(scanner.nextLine());
        if(timeOfDay != 1 && timeOfDay != 2) {
            System.out.println("Invalid selection.");
            return;
        }
        try {
            date = Utils.createLocalDate(year, month, day);
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AVAILABLE_CLASSROOMS_BY_DATE_AND_TIME);
            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.setInt(2, timeOfDay);
            ResultSet resultSet = preparedStatement.executeQuery();
            Utils.printSet(resultSet);
        } catch (DateTimeException dateTimeException) {
            System.out.println("Invalid date entered.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewWeeklyScheduleOfClassroom(Connection connection, Scanner scanner) {
        viewClassrooms(connection);
        System.out.println("Which classroom would you like to view the schedule for?");
        System.out.println("Enter classroom ID: ");
        int classroom_id = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter year for which would like to view classroom schedule: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter month for which would like to view classroom schedule: ");
        int month = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter day for which would like to view classroom schedule: ");
        int day = Integer.parseInt(scanner.nextLine());
        LocalDate date;
        try {
            date = Utils.createLocalDate(year, month, day);
        } catch (DateTimeException dateTimeException) {
            System.out.println("Invalid date entered.");
            return;
        }
        System.out.println("Available times for classroom " + classroom_id + " for week of " + date + ":");
        for(int i = 0; i < 7; i++) {
            LocalDate currentDate = date.plusDays(i);
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CLASSROOM_SCHEDULE);
                preparedStatement.setInt(1, classroom_id);
                preparedStatement.setDate(2, Date.valueOf(currentDate));
                preparedStatement.setInt(3, 1);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    continue;
                }
                System.out.println(currentDate + " AM");
                preparedStatement = connection.prepareStatement(SELECT_CLASSROOM_SCHEDULE);
                preparedStatement.setInt(1, classroom_id);
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

    private void createRange(PreparedStatement preparedStatement,
                             Connection connection,
                             Scanner scanner
    ) {
        int rangeTypeId;
        int rangeCapacity = 15;

        // Grab all range types
        viewRangeTypes(connection);
        System.out.println("Enter range type ID: ");
        rangeTypeId = Integer.parseInt(scanner.nextLine());

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
        int rangeId = Integer.parseInt(scanner.nextLine());
        // verify range exists
        if(!rangeExists(preparedStatement, connection, rangeId)) {
            System.out.println("Range does not exist.");
            return;
        }
        viewRangeTypes(connection);
        System.out.println("Enter new range type ID: ");
        int rangeTypeId = Integer.parseInt(scanner.nextLine());
        // verify bike range type exists
        if(!rangeTypeExists(preparedStatement, connection, rangeTypeId)) {
            System.out.println("Range type does not exist.");
            return;
        }

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(UPDATE_BIKE_RANGE_BY_ID);
            preparedStatement.setInt(1, rangeTypeId);
            preparedStatement.setInt(2, rangeId);
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
            System.out.println(e.getMessage());
        }

    }
    private void deleteRange(PreparedStatement preparedStatement,
                             Connection connection,
                             Scanner scanner
    ) {
        viewRanges(connection);
        System.out.println("Enter range ID to delete: ");
        int rangeId = Integer.parseInt(scanner.nextLine());
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
    private void viewRangeSchedule(Connection connection,
                                   Scanner scanner
    ) {
        int viewRangeScheduleSelection;

        while(true) {
            printViewRangeScheduleSubMenu();
            try {
                viewRangeScheduleSelection = Integer.parseInt(scanner.nextLine());
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
        int year = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter month for which would like to view range availability: ");
        int month = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter day for which would like to view range availability: ");
        int day = Integer.parseInt(scanner.nextLine());
        LocalDate date;
        System.out.println("Would you like to view availability for morning or afternoon?");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        int timeOfDay = Integer.parseInt(scanner.nextLine());
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
        } catch (SQLException e) {
            System.out.println("SQL Exception");
        }
    }

    private void viewWeeklyScheduleOfRange(Connection connection, Scanner scanner) {
        viewRanges(connection);
        System.out.println("Which range would you like to view the schedule for?");
        System.out.println("Enter range ID: ");
        int rangeId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter year for which would like to view range schedule: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter month for which would like to view range schedule: ");
        int month = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter day for which would like to view range schedule: ");
        int day = Integer.parseInt(scanner.nextLine());
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
            }
        }
    }

    private void assignRange(Connection connection, Scanner scanner) {
        System.out.println("Enter year for which would like to view course schedule: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter month for which would like to view course schedule: ");
        int month = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter day for which would like to view course schedule: ");
        int day = Integer.parseInt(scanner.nextLine());
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
            Utils.printSet(courseScheduleResultSet);
        } catch(SQLException e) {
            System.out.println("SQL Exception");
        }

        System.out.println("Enter course schedule ID for which you would like to assign a range: ");
        int courseScheduleId = Integer.parseInt(scanner.nextLine());
        List<Integer> listOfAvailableRanges = new ArrayList<>();
        // get the date of the selected course
        LocalDate courseDate = null;
        try {
            PreparedStatement courseDatePreparedStatement = connection.prepareStatement(SELECT_COURSE_SCHEDULE_DATE_BY_ID);
            courseDatePreparedStatement.setInt(1, courseScheduleId);
            ResultSet courseDateResultSet = courseDatePreparedStatement.executeQuery();
            if(courseDateResultSet.next()) {
                courseDate = courseDateResultSet.getDate("course_date").toLocalDate();
            }
            if(courseDate == null) {
                System.out.println("Could not find course date for ID " + courseScheduleId + ".");
                return;
            }
            // Find the ranges available for the given date
            PreparedStatement availableRangesPreparedStatement = connection.prepareStatement(SELECT_AVAILABLE_RANGE_BY_COURSE_SCHEDULE_ID);
            availableRangesPreparedStatement.setInt(1, courseScheduleId);
            availableRangesPreparedStatement.setDate(2, Date.valueOf(courseDate));
            availableRangesPreparedStatement.setInt(3, courseScheduleId);
            availableRangesPreparedStatement.setDate(4, Date.valueOf(courseDate));
            ResultSet availableRangesResultSet = availableRangesPreparedStatement.executeQuery();
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
            System.out.println("SQL Exception");
        }
        System.out.println("Enter range ID to assign: ");
        int rangeId = Integer.parseInt(scanner.nextLine());

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
            System.out.println("SQL Exception");
        }
    }
    private void unassignRange(Connection connection,
                               Scanner scanner
    ) {
        printRangeAssignments(connection);
        System.out.println("Enter range assignment ID to unassign: ");
        int rangeAssignmentId = Integer.parseInt(scanner.nextLine());
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
            System.out.println("SQL Exception");
        }
    }

    private void viewRangeTypes(Connection connection) {
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM range_type");
            // Print the header
            System.out.println("Range Types");
            Utils.printSet(resultSet);
        } catch (SQLException e) {
            System.out.println("Error during range type retrieval.");
            System.out.println("SQL Exception");
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
            System.out.println("SQL Exception");
        }
        try {
            preparedStatement.setInt(1, rangeId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                rangeExists = true;
            }
        } catch (SQLException e) {
            System.out.println("Error during range retrieval.");
            System.out.println("SQL Exception");
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
            System.out.println("SQL Exception");
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
    private void editRangeType(PreparedStatement preparedStatement,
                               Connection connection,
                               Scanner scanner
    ) {
        viewRangeTypes(connection);
        System.out.println("Enter range type ID to edit: ");
        int rangeTypeId = Integer.parseInt(scanner.nextLine());
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
        int rangeTypeId = Integer.parseInt(scanner.nextLine());
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
            System.out.println("SQL Exception");
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