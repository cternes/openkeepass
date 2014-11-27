package de.slackspace.openkeepass.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import de.slackspace.openkeepass.crypto.Salsa20;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.util.ByteUtils;

public class XmlParserTest {

	private byte[] protectedStreamKey = ByteUtils.hexStringToByteArray("ec77a2169769734c5d26e5341401f8d7b11052058f8455d314879075d0b7e257");
	private static SimpleDateFormat dateFormatter;
	
	@BeforeClass
	public static void init() {
		// make sure we use UTC time
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	@Test
	public void whenInputIsValidKeePassXmlShouldParseFileAndReturnCorrectMetadata() throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream("target/test-classes/testDatabase_decrypted.xml");
		KeePassFile keePassFile = new XmlParser().parse(fileInputStream, Salsa20.createInstance(protectedStreamKey));
		Assert.assertEquals("KeePass", keePassFile.getMeta().getGenerator());
		Assert.assertEquals("TestDatabase", keePassFile.getMeta().getDatabaseName());
		Assert.assertEquals("Just a sample db", keePassFile.getMeta().getDatabaseDescription());
		Assert.assertEquals("2014-11-22 18:59:39", dateFormatter.format(keePassFile.getMeta().getDatabaseNameChanged().getTime()));
		Assert.assertEquals("2014-11-22 18:59:39", dateFormatter.format(keePassFile.getMeta().getDatabaseDescriptionChanged().getTime()));
		Assert.assertEquals(365, keePassFile.getMeta().getMaintenanceHistoryDays());
		Assert.assertEquals(true, keePassFile.getMeta().getRecycleBinEnabled());
		Assert.assertEquals("AAAAAAAAAAAAAAAAAAAAAA==", keePassFile.getMeta().getRecycleBinUuid()); 
		Assert.assertEquals("2014-11-22 18:58:56", dateFormatter.format(keePassFile.getMeta().getRecycleBinChanged().getTime()));
		Assert.assertEquals(10, keePassFile.getMeta().getHistoryMaxItems());
		Assert.assertEquals(6291456, keePassFile.getMeta().getHistoryMaxSize());
	}
	
	@Test
	public void whenInputIsValidKeePassXmlShouldParseFileAndReturnCorrectGroups() throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream("target/test-classes/testDatabase_decrypted.xml");
		KeePassFile keePassFile = new XmlParser().parse(fileInputStream, Salsa20.createInstance(protectedStreamKey));
		
		List<Group> groups = keePassFile.getTopGroups();
		Assert.assertNotNull(groups);
		
		Assert.assertEquals(6, groups.size());
		Assert.assertEquals("General", groups.get(0).getName());
		Assert.assertEquals("FqvMJ8yjlUSAEt9OmNSj2A==", groups.get(0).getUuid());
		
		Assert.assertEquals("Windows", groups.get(1).getName());
		Assert.assertEquals("rXt7D+EM/0qW1rgPB4g5nw==", groups.get(1).getUuid());
		
		Assert.assertEquals("Network", groups.get(2).getName());
		Assert.assertEquals("DwdAaKn4tEyXFlU56/2UBQ==", groups.get(2).getUuid());
		
		Assert.assertEquals("Internet", groups.get(3).getName());
		Assert.assertEquals("COgUrPt5P0676DeyZn/auQ==", groups.get(3).getUuid());
		
		Assert.assertEquals("eMail", groups.get(4).getName());
		Assert.assertEquals("/xWfOfnC6ki76sNhrZR7rw==", groups.get(4).getUuid());
		
		Assert.assertEquals("Homebanking", groups.get(5).getName());
		Assert.assertEquals("Rdjt21Jla0+E5Q9ElJHw1g==", groups.get(5).getUuid());
	}
	
	@Test
	public void whenInputIsValidKeePassXmlShouldParseFileAndReturnCorrectEntries() throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream("target/test-classes/testDatabase_decrypted.xml");
		KeePassFile keePassFile = new XmlParser().parse(fileInputStream, Salsa20.createInstance(protectedStreamKey));
		
		List<Entry> entries = keePassFile.getTopEntries();
		Assert.assertNotNull(entries);
		
		Assert.assertEquals(2, entries.size());
		Assert.assertEquals("libdLW88cU6BvrPQlvKqMA==", entries.get(0).getUuid());
		Assert.assertEquals(5, entries.get(0).getProperties().size());
		Assert.assertEquals("Sample Entry", entries.get(0).getTitle());
		Assert.assertEquals("http://keepass.info/", entries.get(0).getUrl());
		Assert.assertEquals("User Name", entries.get(0).getUsername());
		Assert.assertEquals("Notes", entries.get(0).getNotes());
		Assert.assertEquals("Password", entries.get(0).getPassword());
	}
}
