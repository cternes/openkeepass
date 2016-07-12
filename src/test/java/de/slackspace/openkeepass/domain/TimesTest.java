package de.slackspace.openkeepass.domain;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleXmlParser;
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
        Assert.assertEquals("<times><LastModificationTime>2016-01-18T00:00:00.000Z</LastModificationTime><CreationTime>2016-01-15T00:00:00.000Z</CreationTime><LastAccessTime>2016-01-17T00:00:00.000Z</LastAccessTime><ExpiryTime>2016-01-16T00:00:00.000Z</ExpiryTime><Expires>True</Expires><UsageCount>23</UsageCount><LocationChanged>2016-01-19T00:00:00.000Z</LocationChanged></times>", xml);
    }
}
