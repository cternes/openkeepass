package de.slackspace.openkeepass.xml;

import java.io.ByteArrayInputStream;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;

import de.slackspace.openkeepass.domain.KeyFile;

public class KeyFileXmlParser {

	public KeyFile fromXml(byte[] inputBytes) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
			return JAXB.unmarshal(inputStream, KeyFile.class);
		}
		catch(DataBindingException e) {
			return new KeyFile(false);
		}
	}
}
