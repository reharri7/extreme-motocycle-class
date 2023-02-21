package emc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static LocalDate createLocalDate(int year, int month, int day) throws DateTimeException {
        if(year < 2023 || year > 2100) {
            throw new DateTimeException("Invalid date entered.");
        }
        return LocalDate.of(year, month, day);
    }

    public static void printSet(ResultSet r) {
        // Used later to get metadata from the result set
        ResultSetMetaData rsmd = null;

        // Will hold the number of columns in the result set
        int columnCount = 0;

        // Create a list to later hold the column names from metadata
        List<String> columnNames = new ArrayList<String>();

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
        // Do not modify the loop values: the result sets column index from 1 while a List index starts at 0.  The plus ones are to compensate for this.
        for (int i = 0; i < columnCount; i++) {
            try {
                columnNames.add(rsmd.getColumnName(i + 1));
            } catch (SQLException e) {
                System.out.println("Error getting column name");
                e.printStackTrace();
                return;
            }
        }

        // Print the column names
        for (int i = 0; i < columnCount; i++) {
            int ds = 0;
            try {
                ds = rsmd.getColumnDisplaySize(i + 1);
                if (ds == 0) { // If a display size is unavailable, use the length of the column name plus 5 padding characters
                    ds = columnNames.get(i).length() + 5;
                }
            } catch (SQLException e) {
                System.out.println("Error getting column display size");
                e.printStackTrace();
            }
            // Build the formatting using the display size
            System.out.printf("%-" + ds + "s", columnNames.get(i));
        }
        System.out.println();

        // Print the resultset
        try {
            while (r.next()) {
                for (int i = 0; i < columnCount; i++) {
                    rsmd.getColumnType(columnCount);
                    int ds = rsmd.getColumnDisplaySize(i + 1);
                    System.out.printf("%-" + ds + "s", r.getString(columnNames.get(i)));
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error printing result set");
            e.printStackTrace();
        }
    }

    public static boolean columnExistsInTable(Connection conn, String column, String table) {
        String query = "SELECT " + column + " FROM " + table + "LIMIT 1";
        boolean exists = false;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columns = rsmd.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                if (rsmd.getColumnName(i).equals(column)) {
                    exists = true;
                    break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking column exists.  Please check the column name and table name.");
            e.printStackTrace();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return exists;
    }
}
