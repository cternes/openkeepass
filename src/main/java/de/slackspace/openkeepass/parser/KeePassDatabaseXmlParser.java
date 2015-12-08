package de.slackspace.openkeepass.parser;

import java.io.ByteArrayOutputStream;
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

	public KeePassFile fromXml(InputStream inputStream, ProtectedStringCrypto protectedStringCrypto) {
		KeePassFile keePassFile = JAXB.unmarshal(inputStream, KeePassFile.class);
		keePassFile.init();
		
		processAllProtectedValues(false, protectedStringCrypto, keePassFile);
		
		return keePassFile;
	}
	
	public ByteArrayOutputStream toXml(KeePassFile keePassFile, ProtectedStringCrypto protectedStringCrypto) {
		processAllProtectedValues(true, protectedStringCrypto, keePassFile);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JAXB.marshal(keePassFile, outputStream);
		
		return outputStream;
	}
	
	private void processAllProtectedValues(boolean encrypt, ProtectedStringCrypto protectedStringCrypto, KeePassFile keePassFile) {
		// Decrypt/Encrypt all protected values
		List<Entry> entries = keePassFile.getEntries();
		for (Entry entry : entries) {
			processProtectedValues(encrypt, entry, protectedStringCrypto);
			
			// Also process historic password values 
			History history = entry.getHistory();
			if(history != null) {
				for (Entry historicEntry : history.getHistoricEntries()) {
					processProtectedValues(encrypt, historicEntry, protectedStringCrypto);
				}
			}
		}
	}
	
	private void processProtectedValues(boolean encrypt, Entry entry, ProtectedStringCrypto protectedStringCrypto) {
		List<Property> properties = entry.getProperties();
		for (Property property : properties) {
			PropertyValue propertyValue = property.getPropertyValue();
			
			if(!propertyValue.getValue().isEmpty() && propertyValue.isProtected()) {
				
				String processedValue = null;
				if(encrypt) {
					processedValue = protectedStringCrypto.encrypt(propertyValue.getValue());
				}
				else {
					processedValue = protectedStringCrypto.decrypt(propertyValue.getValue());	
				}
				
				propertyValue.setValue(processedValue);
			}
		}
	}
}
