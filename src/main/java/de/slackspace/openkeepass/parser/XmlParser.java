package de.slackspace.openkeepass.parser;

import java.io.InputStream;

import javax.xml.bind.JAXB;

import de.slackspace.openkeepass.domain.KeePassFile;

public class XmlParser {

	public KeePassFile parse(InputStream inputStream) {
		KeePassFile keePassFile = JAXB.unmarshal(inputStream, KeePassFile.class);
		return keePassFile;
	}
}
