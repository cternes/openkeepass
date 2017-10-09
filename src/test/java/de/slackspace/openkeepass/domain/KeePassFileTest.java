package de.slackspace.openkeepass.domain;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Base64;

import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.util.CalendarHandler;
import de.slackspace.openkeepass.util.XmlStringCleaner;

public class KeePassFileTest {

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

        Entry entryOne = new EntryBuilder("SomeTitle")
                .notes("MyNote")
                .password("MyPasswd")
                .url("http://test.com")
                .username("MyUser")
                .uuid(UUID.fromString("87d4f441-a5ec-4ce0-8ca9-82a5079d28ef"))
                .customIconUuid(UUID.fromString("87d4f441-a5ec-4ce0-8ca9-82a5079d28ef"))
                .iconId(23)
                .iconData(new byte[1])
                .addTag("tag")
                .foregroundColor("#FFFFFF")
                .backgroundColor("#000000")
                .build();

        Group group = new GroupBuilder(UUID.fromString("87d4f441-a5ec-4ce0-8ca9-82a5079d28ef"))
                .addEntry(entryOne).times(times)
                .name("Test")
                .build();

        Meta meta = new MetaBuilder("SomeDb")
                .databaseDescription("some description")
                .databaseDescriptionChanged(CalendarHandler.createCalendar(2016, 1, 15))
                .databaseNameChanged(CalendarHandler.createCalendar(2016, 1, 16))
                .generator("OpenKeePass")
                .historyMaxItems(10)
                .historyMaxSize(20)
                .maintenanceHistoryDays(30)
                .recycleBinChanged(CalendarHandler.createCalendar(2016, 1, 17))
                .recycleBinEnabled(true)
                .recycleBinUuid(UUID.fromString("87d4f441-a5ec-4ce0-8ca9-82a5079d28ef"))
                .build();

        KeePassFile keePassFile = new KeePassFileBuilder(meta)
                .addTopGroups(group)
                .build();

        String rootUuid = Base64.toBase64String(ByteUtils.uuidToBytes(keePassFile.getRoot().getUuid()));

        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(keePassFile);

        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals(
                "<KeePassFile><Meta><Generator>OpenKeePass</Generator><DatabaseName>SomeDb</DatabaseName><DatabaseDescription>some description</DatabaseDescription><DatabaseNameChanged>2016-01-16T00:00:00</DatabaseNameChanged><DatabaseDescriptionChanged>2016-01-15T00:00:00</DatabaseDescriptionChanged><MaintenanceHistoryDays>30</MaintenanceHistoryDays><RecycleBinUUID>h9T0QaXsTOCMqYKlB50o7w==</RecycleBinUUID><RecycleBinChanged>2016-01-17T00:00:00</RecycleBinChanged><RecycleBinEnabled>True</RecycleBinEnabled><HistoryMaxItems>10</HistoryMaxItems><HistoryMaxSize>20</HistoryMaxSize></Meta><Root><UUID>"
                        + rootUuid
                        + "</UUID><IconID>49</IconID><IsExpanded>False</IsExpanded><Group><UUID>h9T0QaXsTOCMqYKlB50o7w==</UUID><Name>Test</Name><IconID>49</IconID><Times><LastModificationTime>2016-01-18T00:00:00</LastModificationTime><CreationTime>2016-01-15T00:00:00</CreationTime><LastAccessTime>2016-01-17T00:00:00</LastAccessTime><ExpiryTime>2016-01-16T00:00:00</ExpiryTime><Expires>True</Expires><UsageCount>23</UsageCount><LocationChanged>2016-01-19T00:00:00</LocationChanged></Times><IsExpanded>False</IsExpanded><Entry><UUID>h9T0QaXsTOCMqYKlB50o7w==</UUID><IconID>23</IconID><CustomIconUUID>h9T0QaXsTOCMqYKlB50o7w==</CustomIconUUID><ForegroundColor>#FFFFFF</ForegroundColor><BackgroundColor>#000000</BackgroundColor><Tags>tag</Tags><String><Key>Notes</Key><Value Protected='False'>MyNote</Value></String><String><Key>Password</Key><Value Protected='True'>MyPasswd</Value></String><String><Key>Title</Key><Value Protected='False'>SomeTitle</Value></String><String><Key>UserName</Key><Value Protected='False'>MyUser</Value></String><String><Key>URL</Key><Value Protected='False'>http://test.com</Value></String></Entry></Group></Root></KeePassFile>",
                xml);
    }
}
