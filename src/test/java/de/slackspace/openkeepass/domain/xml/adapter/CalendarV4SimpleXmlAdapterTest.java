package de.slackspace.openkeepass.domain.xml.adapter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

public class CalendarV4SimpleXmlAdapterTest {

    @Test
    public void shouldDeserializeToCalendar() throws Exception {
        // arrange
        CalendarV4SimpleXmlAdapter adapter = new CalendarV4SimpleXmlAdapter();

        // act
        GregorianCalendar calendar = adapter.read("2hVo0A4AAAA=");

        // assert
        assertThat(calendar.get(Calendar.DATE), is(25));
        assertThat(calendar.get(Calendar.MONTH), is(2));
        assertThat(calendar.get(Calendar.YEAR), is(2017));
        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(7));
        assertThat(calendar.get(Calendar.MINUTE), is(40));
        assertThat(calendar.get(Calendar.SECOND), is(10));
    }

    @Test
    public void shouldSerializeToBase64String() throws Exception {
        // arrange
        CalendarV4SimpleXmlAdapter adapter = new CalendarV4SimpleXmlAdapter();
        GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.set(Calendar.DATE, 25);
        calendar.set(Calendar.MONTH, 2);
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 40);
        calendar.set(Calendar.SECOND, 10);

        // act
        String value = adapter.write(calendar);

        // assert
        assertThat(value, is("2hVo0A4AAAA="));

    }
}
