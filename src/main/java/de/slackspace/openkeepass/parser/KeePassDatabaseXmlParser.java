package de.slackspace.openkeepass.parser;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXB;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.History;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.Property;
import de.slackspace.openkeepass.domain.PropertyValue;

public class KeePassDatabaseXmlParser {

	public KeePassFile parse(InputStream inputStream, ProtectedStringCrypto protectedStringCrypto) {
		KeePassFile keePassFile = JAXB.unmarshal(inputStream, KeePassFile.class);
		keePassFile.init();
		
		// Decrypt all encrypted values
		List<Entry> entries = keePassFile.getEntries();
		for (Entry entry : entries) {
			decryptAndSetValues(entry, protectedStringCrypto);
			
			// Also decrypt historic password values 
			History history = entry.getHistory();
			for (Entry historicEntry : history.getHistoricEntries()) {
				decryptAndSetValues(historicEntry, protectedStringCrypto);
			}
		}
		
		return keePassFile;
	}
	
	private void decryptAndSetValues(Entry entry, ProtectedStringCrypto protectedStringCrypto) {
		List<Property> properties = entry.getProperties();
		for (Property property : properties) {
			PropertyValue propertyValue = property.getPropertyValue();
			
			if(propertyValue.isProtected()) {
				String decrypted = protectedStringCrypto.decrypt(propertyValue.getValue());
				propertyValue.setValue(decrypted);
			}
		}
	}
}
