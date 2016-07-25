package de.slackspace.openkeepass.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Assert;

public class CalendarHandler {

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Creates a new calendar initializes with the given values.
     * <p>
     * Time is always set to 00:00:00
     * 
     * @param year
     *            the year
     * @param month
     *            the month (CAUTION: 1-based)
     * @param day
     *            the day (1-based)
     * @return an initialized calendar
     */
    public static Calendar createCalendar(int year, int month, int day) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    public static String formatCalendar(Calendar cal) {
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatter.format(cal.getTime());
    }
    
    public static void isEqual(Calendar calOne, Calendar calTwo) {
        Assert.assertTrue(String.format("The time of '%s' is not equal to '%s'", calOne.getTime(), calTwo.getTime()), calOne.getTimeInMillis() == calTwo.getTimeInMillis());
    }
}
