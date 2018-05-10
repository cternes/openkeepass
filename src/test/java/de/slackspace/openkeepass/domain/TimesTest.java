package de.slackspace.openkeepass.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.processor.NullProtectionStrategy;
import de.slackspace.openkeepass.util.CalendarHandler;
import de.slackspace.openkeepass.util.XmlStringCleaner;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class TimesTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Times.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        Times times = new TimesBuilder()
            .creationTime(CalendarHandler.createCalendar(2016, 1, 15))
            .expires(true)
            .expiryTime(CalendarHandler.createCalendar(2016, 1, 16))
            .lastAccessTime(CalendarHandler.createCalendar(2016, 1, 17))
            .lastModificationTime(CalendarHandler.createCalendar(2016, 1, 18))
            .locationChanged(CalendarHandler.createCalendar(2016, 1, 19))
            .usageCount(23)
            .build();

        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(times);
        
        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<times><LastModificationTime>2016-01-18T00:00:00</LastModificationTime><CreationTime>2016-01-15T00:00:00</CreationTime><LastAccessTime>2016-01-17T00:00:00</LastAccessTime><ExpiryTime>2016-01-16T00:00:00</ExpiryTime><Expires>True</Expires><UsageCount>23</UsageCount><LocationChanged>2016-01-19T00:00:00</LocationChanged></times>", xml);
    }
    
    @Test
    public void shouldUnmarshallXmlToObject() throws Exception {
        Times times = new TimesBuilder()
            .creationTime(CalendarHandler.createCalendar(2016, 1, 15))
            .expires(true)
            .expiryTime(CalendarHandler.createCalendar(2016, 1, 16))
            .lastAccessTime(CalendarHandler.createCalendar(2016, 1, 17))
            .lastModificationTime(CalendarHandler.createCalendar(2016, 1, 18))
            .locationChanged(CalendarHandler.createCalendar(2016, 1, 19))
            .usageCount(23)
            .build();

        String xml = "<times><LastModificationTime>2016-01-18T00:00:00</LastModificationTime><CreationTime>2016-01-15T00:00:00</CreationTime><LastAccessTime>2016-01-17T00:00:00</LastAccessTime><ExpiryTime>2016-01-16T00:00:00</ExpiryTime><Expires>True</Expires><UsageCount>23</UsageCount><LocationChanged>2016-01-19T00:00:00</LocationChanged></times>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Times timesUnmarshalled = new SimpleXmlParser().fromXml(inputStream, new NullProtectionStrategy(), Times.class);

        Assert.assertEquals(times.getUsageCount(), timesUnmarshalled.getUsageCount());
        CalendarHandler.isEqual(times.getCreationTime(), timesUnmarshalled.getCreationTime());
        CalendarHandler.isEqual(times.getExpiryTime(), timesUnmarshalled.getExpiryTime());
        CalendarHandler.isEqual(times.getLastAccessTime(), timesUnmarshalled.getLastAccessTime());
        CalendarHandler.isEqual(times.getLastModificationTime(), timesUnmarshalled.getLastModificationTime());
        CalendarHandler.isEqual(times.getLocationChanged(), timesUnmarshalled.getLocationChanged());
    }
}
