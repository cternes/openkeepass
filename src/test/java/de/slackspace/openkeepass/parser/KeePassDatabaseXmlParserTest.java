package de.slackspace.openkeepass.parser;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.slackspace.openkeepass.crypto.Salsa20;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.Times;
import de.slackspace.openkeepass.processor.DecryptionStrategy;
import de.slackspace.openkeepass.processor.ProtectedValueProcessor;
import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.xml.KeePassDatabaseXmlParser;

public class KeePassDatabaseXmlParserTest {

	private byte[] protectedStreamKey = ByteUtils
			.hexStringToByteArray("ec77a2169769734c5d26e5341401f8d7b11052058f8455d314879075d0b7e257");
	private static SimpleDateFormat dateFormatter;

	@BeforeClass
	public static void init() {
		// make sure we use UTC time
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Test
	public void whenInputIsValidKeePassXmlShouldParseFileAndReturnCorrectMetadata() throws FileNotFoundException {
		KeePassFile keePassFile = parseKeePassXml();
		Assert.assertEquals("KeePass", keePassFile.getMeta().getGenerator());
		Assert.assertEquals("TestDatabase", keePassFile.getMeta().getDatabaseName());
		Assert.assertEquals("Just a sample db", keePassFile.getMeta().getDatabaseDescription());
		Assert.assertEquals("2014-11-22 18:59:39",
				dateFormatter.format(keePassFile.getMeta().getDatabaseNameChanged().getTime()));
		Assert.assertEquals("2014-11-22 18:59:39",
				dateFormatter.format(keePassFile.getMeta().getDatabaseDescriptionChanged().getTime()));
		Assert.assertEquals(365, keePassFile.getMeta().getMaintenanceHistoryDays());
		Assert.assertEquals(true, keePassFile.getMeta().getRecycleBinEnabled());
		Assert.assertEquals(UUID.fromString("00000000-0000-0000-0000-00000000"),
				keePassFile.getMeta().getRecycleBinUuid());
		Assert.assertEquals("2014-11-22 18:58:56",
				dateFormatter.format(keePassFile.getMeta().getRecycleBinChanged().getTime()));
		Assert.assertEquals(10, keePassFile.getMeta().getHistoryMaxItems());
		Assert.assertEquals(6291456, keePassFile.getMeta().getHistoryMaxSize());
	}

	@Test
	public void whenInputIsValidKeePassXmlShouldParseFileAndReturnCorrectGroups() throws FileNotFoundException {
		KeePassFile keePassFile = parseKeePassXml();

		List<Group> groups = keePassFile.getTopGroups();
		Assert.assertNotNull(groups);

		Assert.assertEquals(6, groups.size());
		Assert.assertEquals("General", groups.get(0).getName());
		Assert.assertEquals(UUID.fromString("16abcc27-cca3-9544-8012-df4e98d4a3d8"), groups.get(0).getUuid());

		Assert.assertEquals("Windows", groups.get(1).getName());
		Assert.assertEquals(UUID.fromString("ad7b7b0f-e10c-ff4a-96d6-b80f0788399f"), groups.get(1).getUuid());

		Assert.assertEquals("Network", groups.get(2).getName());
		Assert.assertEquals(UUID.fromString("0f074068-a9f8-b44c-9716-5539ebfd9405"), groups.get(2).getUuid());

		Assert.assertEquals("Internet", groups.get(3).getName());
		Assert.assertEquals(UUID.fromString("08e814ac-fb79-3f4e-bbe8-37b2667fdab9"), groups.get(3).getUuid());

		Assert.assertEquals("eMail", groups.get(4).getName());
		Assert.assertEquals(UUID.fromString("ff159f39-f9c2-ea48-bbea-c361ad947baf"), groups.get(4).getUuid());

		Assert.assertEquals("Homebanking", groups.get(5).getName());
		Assert.assertEquals(UUID.fromString("45d8eddb-5265-6b4f-84e5-0f449491f0d6"), groups.get(5).getUuid());
	}

	@Test
	public void whenInputIsValidKeePassXmlShouldParseFileAndReturnCorrectEntries() throws FileNotFoundException {
		KeePassFile keePassFile = parseKeePassXml();

		List<Entry> entries = keePassFile.getTopEntries();
		Assert.assertNotNull(entries);

		Assert.assertEquals(2, entries.size());
		Assert.assertEquals(UUID.fromString("9626dd2d-6f3c-714e-81be-b3d096f2aa30"), entries.get(0).getUuid());
		Assert.assertEquals(5, entries.get(0).getProperties().size());
		Assert.assertEquals("Sample Entry", entries.get(0).getTitle());
		Assert.assertEquals("http://keepass.info/", entries.get(0).getUrl());
		Assert.assertEquals("User Name", entries.get(0).getUsername());
		Assert.assertEquals("Notes", entries.get(0).getNotes());
		Assert.assertEquals("Password", entries.get(0).getPassword());
	}

	@Test
	public void whenUsingGetEntriesShouldReturnAllEntries() throws FileNotFoundException {
		KeePassFile keePassFile = parseKeePassXml();

		List<Entry> entries = keePassFile.getEntries();
		Assert.assertEquals(3, entries.size());
	}

	@Test
	public void whenUsingGetEntriesByTitleExactlyShouldReturnAllEntriesWithGivenTitle() throws FileNotFoundException {
		KeePassFile keePassFile = parseKeePassXml();

		List<Entry> entries = keePassFile.getEntriesByTitle("Sample Entry", true);
		Assert.assertEquals(1, entries.size());
		Assert.assertEquals("Sample Entry", entries.get(0).getTitle());
	}

	@Test
	public void whenUsingGetEntriesByTitleLooselyShouldReturnAllEntriesWithGivenTitle() throws FileNotFoundException {
		KeePassFile keePassFile = parseKeePassXml();

		List<Entry> entries = keePassFile.getEntriesByTitle("Sample Entry", false);
		Assert.assertEquals(2, entries.size());
		Assert.assertEquals("Sample Entry", entries.get(0).getTitle());
		Assert.assertEquals("Sample Entry #2", entries.get(1).getTitle());
	}

	@Test
	public void whenUsingGetEntriesByTitleLooselyButNothingMatchesShouldReturnEmptyList() throws FileNotFoundException {
		KeePassFile keePassFile = parseKeePassXml();

		List<Entry> entries = keePassFile.getEntriesByTitle("abcdefg", false);
		Assert.assertEquals(0, entries.size());
	}

	private KeePassFile parseKeePassXml() throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream("target/test-classes/testDatabase_decrypted.xml");
		KeePassFile keePassFile = new KeePassDatabaseXmlParser().fromXml(fileInputStream,
				Salsa20.createInstance(protectedStreamKey));

		new ProtectedValueProcessor().processProtectedValues(new DecryptionStrategy(Salsa20.createInstance(protectedStreamKey)), keePassFile);

		return keePassFile;
	}

	@Test
	public void whenWritingKeePassFileShouldBeAbleToReadItAgain() throws IOException {
		// Read decrypted and write again
		FileInputStream fileInputStream = new FileInputStream("target/test-classes/testDatabase_decrypted.xml");
		KeePassDatabaseXmlParser parser = new KeePassDatabaseXmlParser();
		KeePassFile keePassFile = parser.fromXml(fileInputStream, Salsa20.createInstance(protectedStreamKey));
		new ProtectedValueProcessor().processProtectedValues(new DecryptionStrategy(Salsa20.createInstance(protectedStreamKey)), keePassFile);

		ByteArrayOutputStream outputStream = parser.toXml(keePassFile, Salsa20.createInstance(protectedStreamKey));
		OutputStream fileOutputStream = new FileOutputStream("target/test-classes/testDatabase_decrypted2.xml");
		outputStream.writeTo(fileOutputStream);

		// Read written file
		FileInputStream writtenInputStream = new FileInputStream("target/test-classes/testDatabase_decrypted2.xml");
		KeePassFile writtenKeePassFile = parser.fromXml(writtenInputStream, Salsa20.createInstance(protectedStreamKey));
		new ProtectedValueProcessor().processProtectedValues(new DecryptionStrategy(Salsa20.createInstance(protectedStreamKey)), writtenKeePassFile);

		Assert.assertEquals("Password", writtenKeePassFile.getEntryByTitle("Sample Entry").getPassword());
	}

	@Test
	public void whenUsingGetGroupsShouldReturnAllGroups() throws FileNotFoundException {
		KeePassFile keePassFile = parseKeePassXml();

		List<Group> groups = keePassFile.getGroups();
		Assert.assertEquals(7, groups.size());
	}

	@Test
	public void whenUsingGetGroupsByNameExactlyShouldReturnGroupsWithGivenName() throws FileNotFoundException {
		KeePassFile keePassFile = parseKeePassXml();

		List<Group> groups = keePassFile.getGroupsByName("Windows", true);
		Assert.assertEquals(1, groups.size());
		Assert.assertEquals("Windows", groups.get(0).getName());
	}

	@Test
	public void whenUsingGetGroupsByNameLooslyShouldReturnGroupsWithGivenName() throws FileNotFoundException {
		KeePassFile keePassFile = parseKeePassXml();

		List<Group> groups = keePassFile.getGroupsByName("net", false);
		Assert.assertEquals(2, groups.size());
		Assert.assertEquals("Network", groups.get(0).getName());
		Assert.assertEquals("Internet", groups.get(1).getName());
	}

	@Test
	public void whenUsingGetGroupByNameShouldReturnOneGroup() throws FileNotFoundException {
		KeePassFile keePassFile = parseKeePassXml();

		Group group = keePassFile.getGroupByName("Internet");
		Assert.assertEquals("Internet", group.getName());
	}

	@Test
	public void whenUsingGetTimesShouldReturnCorrectlyParsedTimes() throws FileNotFoundException {
		KeePassFile keePassFile = parseKeePassXml();

		Group group = keePassFile.getGroupByName("testDatabase");
		Times times = group.getTimes();

		Assert.assertEquals(false, times.expires());
		Assert.assertEquals("2014-11-22 18:58:56", dateFormatter.format(times.getLastModificationTime().getTime()));
		Assert.assertEquals("2014-11-22 18:58:56", dateFormatter.format(times.getCreationTime().getTime()));
		Assert.assertEquals("2014-11-22 18:58:13", dateFormatter.format(times.getExpiryTime().getTime()));
		Assert.assertEquals("2014-11-22 18:59:53", dateFormatter.format(times.getLastAccessTime().getTime()));
		Assert.assertEquals("2014-11-22 18:58:56", dateFormatter.format(times.getLastModificationTime().getTime()));
		Assert.assertEquals("2014-11-22 18:58:56", dateFormatter.format(times.getLocationChanged().getTime()));
		Assert.assertEquals(8, times.getUsageCount());
	}
}
