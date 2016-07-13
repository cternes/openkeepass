package de.slackspace.openkeepass.domain;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.util.CalendarHandler;
import de.slackspace.openkeepass.util.XmlStringCleaner;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class EntryTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Entry.class).suppress(Warning.NONFINAL_FIELDS).verify();
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
        
        Entry entry = new EntryBuilder("SomeTitle")
            .notes("MyNote")
            .password("MyPasswd")
            .url("http://test.com")
            .username("MyUser")
            .uuid(UUID.fromString("87d4f441-a5ec-4ce0-8ca9-82a5079d28ef"))
            .customIconUuid(UUID.fromString("87d4f441-a5ec-4ce0-8ca9-82a5079d28ef"))
            .iconId(23)
            .iconData(new byte[1])
            .times(times)
            .build();

        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(entry);
        
        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<entry><UUID>h9T0QaXsTOCMqYKlB50o7w==</UUID><IconID>23</IconID><CustomIconUUID>h9T0QaXsTOCMqYKlB50o7w==</CustomIconUUID><String><Key>Notes</Key><Value Protected='False'>MyNote</Value></String><String><Key>Password</Key><Value Protected='True'>MyPasswd</Value></String><String><Key>Title</Key><Value Protected='False'>SomeTitle</Value></String><String><Key>UserName</Key><Value Protected='False'>MyUser</Value></String><String><Key>URL</Key><Value Protected='False'>http://test.com</Value></String><Times><LastModificationTime>2016-01-18T00:00:00</LastModificationTime><CreationTime>2016-01-15T00:00:00</CreationTime><LastAccessTime>2016-01-17T00:00:00</LastAccessTime><ExpiryTime>2016-01-16T00:00:00</ExpiryTime><Expires>True</Expires><UsageCount>23</UsageCount><LocationChanged>2016-01-19T00:00:00</LocationChanged></Times></entry>", xml);
    }
}
