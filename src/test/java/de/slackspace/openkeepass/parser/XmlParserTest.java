package de.slackspace.openkeepass.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.Assert;

import org.junit.Test;

import de.slackspace.openkeepass.domain.KeePassFile;

public class XmlParserTest {

	@Test
	public void whenInputIsValidKeePassXmlShouldParseFileAndReturnCorrectMetadata() throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream("target/test-classes/testDatabase_decrypted.xml");
		KeePassFile keePassFile = new XmlParser().parse(fileInputStream);
		Assert.assertEquals("TestDatabase", keePassFile.getMeta().getDatabaseName());
		Assert.assertEquals("Just a sample db", keePassFile.getMeta().getDatabaseDescription());
	}
}
