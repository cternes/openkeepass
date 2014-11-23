package de.slackspace.openkeepass.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.KeePassFile;

public class XmlParserTest {

	@Test
	public void whenInputIsValidKeePassXmlShouldParseFileAndReturnCorrectMetadata() throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream("target/test-classes/testDatabase_decrypted.xml");
		KeePassFile keePassFile = new XmlParser().parse(fileInputStream);
		Assert.assertEquals("TestDatabase", keePassFile.getMeta().getDatabaseName());
		Assert.assertEquals("Just a sample db", keePassFile.getMeta().getDatabaseDescription());
	}
	
	@Test
	public void whenInputIsValidKeePassXmlShouldParseFileAndReturnCorrectGroups() throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream("target/test-classes/testDatabase_decrypted.xml");
		KeePassFile keePassFile = new XmlParser().parse(fileInputStream);
		
		List<Group> groups = keePassFile.getGroups();
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
		KeePassFile keePassFile = new XmlParser().parse(fileInputStream);
		
		List<Entry> entries = keePassFile.getEntries();
		Assert.assertNotNull(entries);
		
		Assert.assertEquals(2, entries.size());
		Assert.assertEquals("libdLW88cU6BvrPQlvKqMA==", entries.get(0).getUuid());
		Assert.assertEquals(5, entries.get(0).getProperties().size());
		Assert.assertEquals("Sample Entry", entries.get(0).getTitle());
		Assert.assertEquals("http://keepass.info/", entries.get(0).getUrl());
		Assert.assertEquals("User Name", entries.get(0).getUsername());
		Assert.assertEquals("Notes", entries.get(0).getNotes());
//		Assert.assertEquals("Password", entries.get(0).getPassword());
	}
}
