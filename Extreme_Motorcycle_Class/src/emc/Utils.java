package emc;

import java.time.DateTimeException;
import java.time.LocalDate;

public class Utils {
    public static LocalDate createLocalDate(int year, int month, int day) throws DateTimeException {
        if(year < 2023 || year > 2100) {
            throw new DateTimeException("Invalid date entered.");
        }
        return LocalDate.of(year, month, day);
    }
}
