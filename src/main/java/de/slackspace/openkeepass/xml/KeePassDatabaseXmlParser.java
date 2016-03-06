package de.slackspace.openkeepass.xml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.bind.JAXB;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.enricher.IconEnricher;
import de.slackspace.openkeepass.processor.DecryptionStrategy;
import de.slackspace.openkeepass.processor.EncryptionStrategy;
import de.slackspace.openkeepass.processor.ProtectedValueProcessor;

public class KeePassDatabaseXmlParser {

	public KeePassFile fromXml(InputStream inputStream, ProtectedStringCrypto protectedStringCrypto) {
		KeePassFile keePassFile = JAXB.unmarshal(inputStream, KeePassFile.class);

		new ProtectedValueProcessor().processProtectedValues(new DecryptionStrategy(), protectedStringCrypto, keePassFile);

		keePassFile = new IconEnricher().enrichNodesWithIconData(keePassFile);

		return keePassFile;
	}

	public ByteArrayOutputStream toXml(KeePassFile keePassFile, ProtectedStringCrypto protectedStringCrypto) {
		new ProtectedValueProcessor().processProtectedValues(new EncryptionStrategy(), protectedStringCrypto, keePassFile);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JAXB.marshal(keePassFile, outputStream);

		return outputStream;
	}


}
