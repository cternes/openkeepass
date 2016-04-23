package de.slackspace.openkeepass.domain.builder;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.domain.Times;
import de.slackspace.openkeepass.domain.TimesBuilder;
import de.slackspace.openkeepass.util.CalendarHandler;

public class TimesBuilderTest {

    @Test
    public void shouldBuildTimesWithCreationTime() {
        Times times = new TimesBuilder().creationTime(CalendarHandler.createCalendar(2015, 1, 1)).build();

        Assert.assertEquals("2015-01-01 00:00:00", CalendarHandler.formatCalendar(times.getCreationTime()));
    }

    @Test
    public void shouldBuildTimesWithExpiryTime() {
        Times times = new TimesBuilder().expires(true).expiryTime(CalendarHandler.createCalendar(2015, 5, 6)).build();

        Assert.assertTrue(times.expires());
        Assert.assertEquals("2015-05-06 00:00:00", CalendarHandler.formatCalendar(times.getExpiryTime()));
    }

    @Test
    public void shouldBuildTimesWithLastAccessAndLastModification() {
        Times times = new TimesBuilder().lastAccessTime(CalendarHandler.createCalendar(2015, 6, 6))
                .lastModificationTime(CalendarHandler.createCalendar(2015, 10, 23)).build();

        Assert.assertEquals("2015-06-06 00:00:00", CalendarHandler.formatCalendar(times.getLastAccessTime()));
        Assert.assertEquals("2015-10-23 00:00:00", CalendarHandler.formatCalendar(times.getLastModificationTime()));
    }

    @Test
    public void shouldBuildTimesWithLocationChanged() {
        Times times = new TimesBuilder().locationChanged(CalendarHandler.createCalendar(2015, 10, 10)).build();

        Assert.assertEquals("2015-10-10 00:00:00", CalendarHandler.formatCalendar(times.getLocationChanged()));
    }

    @Test
    public void shouldBuildTimesWithUsageCount() {
        Times times = new TimesBuilder().usageCount(10).build();

        Assert.assertEquals(10, times.getUsageCount());
    }

    @Test
    public void shouldCheckEqualityOfObjects() {
        Times times = new TimesBuilder().creationTime(Calendar.getInstance()).expires(true).expiryTime(Calendar.getInstance())
                .lastAccessTime(Calendar.getInstance()).lastModificationTime(Calendar.getInstance()).locationChanged(Calendar.getInstance()).usageCount(20)
                .build();

        Times timesCopyEqual = new TimesBuilder(times).build();
        Assert.assertEquals(times, timesCopyEqual);

        Times timesCopyNotEqual = new TimesBuilder(times).usageCount(25).build();
        Assert.assertNotEquals(times, timesCopyNotEqual);
    }
}
