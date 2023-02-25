package emc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static LocalDate createLocalDate(int year, int month, int day) throws DateTimeException {
        if (year < 2023 || year > 2100) {
            throw new DateTimeException("Invalid date entered.");
        }
        return LocalDate.of(year, month, day);
    }

    public static LocalDate createBirthday(int year, int month, int day) throws DateTimeException {
        if (year > 2023) {
            throw new DateTimeException("Invalid date entered.");
        }
        return LocalDate.of(year, month, day);
    }


    /**
     * @param r
     */
    public static void printSet(ResultSet r) {
        // Used later to get metadata from the result set
        ResultSetMetaData rsmd;

        // Will hold the number of columns in the result set
        int columnCount;

        // Create a list to later hold the column names from metadata
        List<String> columnNames = new ArrayList<>();

        // Load metadata from the result set and get column count while we're at it
        try {
            rsmd = r.getMetaData();
            columnCount = rsmd.getColumnCount();
        } catch (SQLException e) {
            System.out.println("Error getting metadata");
            e.printStackTrace();
            return;
        }

        // Read the column names
        // Do not modify the loop values: the result sets column index from 1 while a
        // List index starts at 0. The plus ones are to compensate for this.
        for (int i = 0; i < columnCount; i++) {
            try {
                columnNames.add(rsmd.getColumnName(i + 1).trim());
            } catch (SQLException e) {
                System.out.println("Error getting column name");
                e.printStackTrace();
                return;
            }
        }

        // Create an array to hold the maximum length of each column for formatting
        // purposes
        int[] colMax = new int[columnCount];
        // Load the array with the length of the column names initially and then
        // increase as needed when reading the result set
        for (int i = 0; i < columnCount; i++) {
            colMax[i] = columnNames.get(i).length() + 5;
        }

        // Read the result into a 2D array list of strings
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        try {
            while (r.next()) {
                ArrayList<String> row = new ArrayList<>();
                for (int i = 0; i < columnCount; i++) {
                    try {
                        row.add(r.getString(i + 1).trim());
                        if (r.getString(i + 1).trim().length() > colMax[i]) {
                            colMax[i] = r.getString(i + 1).trim().length() + 5;
                        }
                    } catch (SQLException e) {
                        System.out.println("Error getting column value");
                        e.printStackTrace();
                        return;
                    }
                }
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        printColumnNames(columnNames, colMax);
        printDivider(colMax);
        printContents(result, colMax);

    }


    /**
     * @param colNames
     * @param colMax
     */
    private static void printColumnNames(List<String> colNames, int[] colMax) {
        // Print the column names
        for (int i = 0; i < colMax.length; i++) {
            System.out.print("|");
            System.out.printf("%" + colMax[i] + "s", colNames.get(i));
        }
        System.out.println("|");
    }


    /**
     * @param colMax
     */
    private static void printDivider(int[] colMax) {
        for (int max : colMax) {
            System.out.print("|");
            for (int j = 0; j < max; j++) {
                System.out.print("-");
            }
        }
        System.out.println("|");
    }


    /**
     * @param result
     * @param colMax
     */
    private static void printContents(ArrayList<ArrayList<String>> result, int[] colMax) {
        // Print the 2d array list of strings
        for (ArrayList<String> strings : result) {
            System.out.print("|");
            for (int j = 0; j < strings.size(); j++) {
                System.out.printf("%" + colMax[j] + "s", strings.get(j));
                System.out.print("|");
            }
            System.out.println();
        }
    }
}