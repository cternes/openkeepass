package de.slackspace.openkeepass.xml;

import java.io.InputStream;

import javax.xml.bind.JAXB;

import de.slackspace.openkeepass.domain.KeyFile;

public class KeyFileXmlParser {

	public KeyFile fromXml(InputStream inputStream) {
		KeyFile keyFile = JAXB.unmarshal(inputStream, KeyFile.class);

		return keyFile;
	}
}
