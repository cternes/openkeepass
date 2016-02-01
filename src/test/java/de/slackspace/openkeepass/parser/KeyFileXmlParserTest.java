package de.slackspace.openkeepass.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.domain.KeyFile;
import de.slackspace.openkeepass.xml.KeyFileXmlParser;

public class KeyFileXmlParserTest {

	@Test
	public void whenInputIsKeyFileShouldParseFileAndReturnCorrectData() throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream("target/test-classes/DatabaseWithKeyfile.key");
		KeyFile keyFile = new KeyFileXmlParser().fromXml(fileInputStream);

		Assert.assertEquals("RP+rYNZL4lrGtDMBPzOuctlh3NAutSG5KGsT38C+qPQ=", keyFile.getKey().getData());
	}
}
