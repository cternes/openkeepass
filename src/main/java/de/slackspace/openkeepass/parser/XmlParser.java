package de.slackspace.openkeepass.parser;

import java.io.InputStream;

import javax.xml.bind.JAXB;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;
import de.slackspace.openkeepass.crypto.Salsa20;
import de.slackspace.openkeepass.domain.KeePassFile;

public class XmlParser {

	public KeePassFile parse(InputStream inputStream, ProtectedStringCrypto protectedStringCrypto) {
		KeePassFile keePassFile = JAXB.unmarshal(inputStream, KeePassFile.class);
		keePassFile.init(protectedStringCrypto);
		return keePassFile;
	}
}
