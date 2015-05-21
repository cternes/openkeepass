package de.slackspace.openkeepass.parser;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXB;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.History;
import de.slackspace.openkeepass.domain.KeePassFile;

public class KeePassDatabaseXmlParser {

	public KeePassFile parse(InputStream inputStream, ProtectedStringCrypto protectedStringCrypto) {
		KeePassFile keePassFile = JAXB.unmarshal(inputStream, KeePassFile.class);
		keePassFile.init();
		
		// Decrypt all encrypted values
		List<Entry> entries = keePassFile.getEntries();
		for (Entry entry : entries) {
			decryptAndSetPassword(entry, protectedStringCrypto);
			
			// Also decrypt historic password values 
			History history = entry.getHistory();
			for (Entry historicEntry : history.getHistoricEntries()) {
				decryptAndSetPassword(historicEntry, protectedStringCrypto);
			}
		}
		
		return keePassFile;
	}
	
	private void decryptAndSetPassword(Entry entry, ProtectedStringCrypto protectedStringCrypto) {
		if(entry != null && entry.getPassword() != null && !entry.getPassword().isEmpty() && entry.isPasswordProtected()) {
			String decrypted = protectedStringCrypto.decrypt(entry.getPassword());
			entry.setPassword(decrypted);
		}
	}
}
