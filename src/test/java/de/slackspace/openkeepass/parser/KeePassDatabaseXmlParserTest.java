package de.slackspace.openkeepass.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.slackspace.openkeepass.crypto.Salsa20;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.Times;
import de.slackspace.openkeepass.processor.DecryptionStrategy;
import de.slackspace.openkeepass.processor.ProtectedValueProcessor;
import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.util.ResourceUtils;

public class KeePassDatabaseXmlParserTest {

    private byte[] protectedStreamKey = ByteUtils.hexStringToByteArray("ec77a2169769734c5d26e5341401f8d7b11052058f8455d314879075d0b7e257");

    private static SimpleDateFormat dateFormatter;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void init() {
        // make sure we use UTC time
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Test
    public void whenInputIsValidKeePassXmlShouldParseFileAndReturnCorrectMetadata() throws FileNotFoundException {
        KeePassFile keePassFile = parseKeePassXml();
        assertThat(keePassFile.getMeta().getGenerator(), is("KeePass"));
        assertThat(keePassFile.getMeta().getDatabaseName(), is("TestDatabase"));
        assertThat(keePassFile.getMeta().getDatabaseDescription(), is("Just a sample db"));
        assertThat(dateFormatter.format(keePassFile.getMeta().getDatabaseNameChanged().getTime()), is("2014-11-22 18:59:39"));
        assertThat(dateFormatter.format(keePassFile.getMeta().getDatabaseDescriptionChanged().getTime()), is("2014-11-22 18:59:39"));
        assertThat(keePassFile.getMeta().getMaintenanceHistoryDays(), is(365));
        assertThat(keePassFile.getMeta().getRecycleBinEnabled(), is(true));
        assertThat(keePassFile.getMeta().getRecycleBinUuid(), is(UUID.fromString("00000000-0000-0000-0000-00000000")));
        assertThat(dateFormatter.format(keePassFile.getMeta().getRecycleBinChanged().getTime()), is("2014-11-22 18:58:56"));
        assertThat(keePassFile.getMeta().getHistoryMaxItems(), is(10L));
        assertThat(keePassFile.getMeta().getHistoryMaxSize(), is(6291456L));
    }

    @Test
    public void whenInputIsValidKeePassXmlShouldParseFileAndReturnCorrectGroups() throws FileNotFoundException {
        KeePassFile keePassFile = parseKeePassXml();

        List<Group> groups = keePassFile.getTopGroups();
        Assert.assertNotNull(groups);

        assertThat(groups.size(), is(6));
        assertThat(groups.get(0).getName(), is("General"));
        assertThat(groups.get(0).getUuid(), is(UUID.fromString("16abcc27-cca3-9544-8012-df4e98d4a3d8")));

        assertThat(groups.get(1).getName(), is("Windows"));
        assertThat(groups.get(1).getUuid(), is(UUID.fromString("ad7b7b0f-e10c-ff4a-96d6-b80f0788399f")));

        assertThat(groups.get(2).getName(), is("Network"));
        assertThat(groups.get(2).getUuid(), is(UUID.fromString("0f074068-a9f8-b44c-9716-5539ebfd9405")));

        assertThat(groups.get(3).getName(), is("Internet"));
        assertThat(groups.get(3).getUuid(), is(UUID.fromString("08e814ac-fb79-3f4e-bbe8-37b2667fdab9")));

        assertThat(groups.get(4).getName(), is("eMail"));
        assertThat(groups.get(4).getUuid(), is(UUID.fromString("ff159f39-f9c2-ea48-bbea-c361ad947baf")));

        assertThat(groups.get(5).getName(), is("Homebanking"));
        assertThat(groups.get(5).getUuid(), is(UUID.fromString("45d8eddb-5265-6b4f-84e5-0f449491f0d6")));
    }

    @Test
    public void whenInputIsValidKeePassXmlShouldParseFileAndReturnCorrectEntries() throws FileNotFoundException {
        KeePassFile keePassFile = parseKeePassXml();

        List<Entry> entries = keePassFile.getTopEntries();
        Assert.assertNotNull(entries);

        assertThat(entries.size(), is(2));
        Entry entry = entries.get(0);
        assertThat(entry.getUuid(), is(UUID.fromString("9626dd2d-6f3c-714e-81be-b3d096f2aa30")));
        assertThat(entry.getProperties().size(), is(5));
        assertThat(entry.getTitle(), is("Sample Entry"));
        assertThat(entry.getUrl(), is("http://keepass.info/"));
        assertThat(entry.getUsername(), is("User Name"));
        assertThat(entry.getNotes(), is("Notes"));
        assertThat(entry.getPassword(), is("Password"));
    }

    @Test
    public void whenUsingGetEntriesShouldReturnAllEntries() throws FileNotFoundException {
        KeePassFile keePassFile = parseKeePassXml();

        List<Entry> entries = keePassFile.getEntries();
        assertThat(entries.size(), is(3));
    }

    @Test
    public void whenUsingGetEntriesByTitleExactlyShouldReturnAllEntriesWithGivenTitle() throws FileNotFoundException {
        KeePassFile keePassFile = parseKeePassXml();

        List<Entry> entries = keePassFile.getEntriesByTitle("Sample Entry", true);
        assertThat(entries.size(), is(1));
        assertThat(entries.get(0).getTitle(), is("Sample Entry"));
    }

    @Test
    public void whenUsingGetEntriesByTitleLooselyShouldReturnAllEntriesWithGivenTitle() throws FileNotFoundException {
        KeePassFile keePassFile = parseKeePassXml();

        List<Entry> entries = keePassFile.getEntriesByTitle("Sample Entry", false);
        assertThat(entries.size(), is(2));
        assertThat(entries.get(0).getTitle(), is("Sample Entry"));
        assertThat(entries.get(1).getTitle(), is("Sample Entry #2"));
    }

    @Test
    public void whenUsingGetEntriesByTitleLooselyButNothingMatchesShouldReturnEmptyList() throws FileNotFoundException {
        KeePassFile keePassFile = parseKeePassXml();

        List<Entry> entries = keePassFile.getEntriesByTitle("abcdefg", false);
        assertThat(entries.size(), is(0));
    }

    @Test
    public void whenWritingKeePassFileShouldBeAbleToReadItAgain() throws IOException {
        // Read decrypted and write again
        FileInputStream fileInputStream = new FileInputStream(ResourceUtils.getResource("testDatabase_decrypted.xml"));
        KeePassDatabaseXmlParser parser = new KeePassDatabaseXmlParser(new SimpleXmlParser());
        KeePassFile keePassFile = parser.fromXml(fileInputStream);

        String testDatabase_decrypted2 = tempFolder.newFile("testDatabase_decrypted2.xml").getPath();

        ByteArrayOutputStream outputStream = parser.toXml(keePassFile);
        OutputStream fileOutputStream = new FileOutputStream(testDatabase_decrypted2);
        outputStream.writeTo(fileOutputStream);

        // Read written file
        FileInputStream writtenInputStream = new FileInputStream(testDatabase_decrypted2);
        KeePassFile writtenKeePassFile = parser.fromXml(writtenInputStream);
        new ProtectedValueProcessor().processProtectedValues(new DecryptionStrategy(Salsa20.createInstance(protectedStreamKey)), writtenKeePassFile);

        assertThat(writtenKeePassFile.getEntryByTitle("Sample Entry").getPassword(), is("Password"));
    }

    @Test
    public void whenUsingGetGroupsShouldReturnAllGroups() throws FileNotFoundException {
        KeePassFile keePassFile = parseKeePassXml();

        List<Group> groups = keePassFile.getGroups();
        assertThat(groups.size(), is(7));
    }

    @Test
    public void whenUsingGetGroupsByNameExactlyShouldReturnGroupsWithGivenName() throws FileNotFoundException {
        KeePassFile keePassFile = parseKeePassXml();

        List<Group> groups = keePassFile.getGroupsByName("Windows", true);
        assertThat(groups.size(), is(1));
        assertThat(groups.get(0).getName(), is("Windows"));
    }

    @Test
    public void whenUsingGetGroupsByNameLooslyShouldReturnGroupsWithGivenName() throws FileNotFoundException {
        KeePassFile keePassFile = parseKeePassXml();

        List<Group> groups = keePassFile.getGroupsByName("net", false);
        assertThat(groups.size(), is(2));
        assertThat(groups.get(0).getName(), is("Network"));
        assertThat(groups.get(1).getName(), is("Internet"));
    }

    @Test
    public void whenUsingGetGroupByNameShouldReturnOneGroup() throws FileNotFoundException {
        KeePassFile keePassFile = parseKeePassXml();

        Group group = keePassFile.getGroupByName("Internet");
        assertThat(group.getName(), is("Internet"));
    }

    @Test
    public void whenUsingGetTimesShouldReturnCorrectlyParsedTimes() throws FileNotFoundException {
        KeePassFile keePassFile = parseKeePassXml();

        Group group = keePassFile.getGroupByName("testDatabase");
        Times times = group.getTimes();

        assertThat(times.expires(), is(false));
        assertThat(toString(times.getLastModificationTime()), is("2014-11-22 18:58:56"));
        assertThat(toString(times.getCreationTime()), is("2014-11-22 18:58:56"));
        assertThat(toString(times.getExpiryTime()), is("2014-11-22 18:58:13"));
        assertThat(toString(times.getLastAccessTime()), is("2014-11-22 18:59:53"));
        assertThat(toString(times.getLocationChanged()), is("2014-11-22 18:58:56"));
        assertThat(times.getUsageCount(), is(8));
    }

    private String toString(Calendar calendar) {
        return dateFormatter.format(calendar.getTime());
    }

    private KeePassFile parseKeePassXml() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(ResourceUtils.getResource("testDatabase_decrypted.xml"));
        KeePassFile keePassFile = new KeePassDatabaseXmlParser(new SimpleXmlParser()).fromXml(fileInputStream);

        new ProtectedValueProcessor().processProtectedValues(new DecryptionStrategy(Salsa20.createInstance(protectedStreamKey)), keePassFile);

        return keePassFile;
    }
}
