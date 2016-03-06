package de.slackspace.openkeepass.processor;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;

public class EncryptionStrategy implements ProtectionStrategy {

	@Override
	public String apply(ProtectedStringCrypto crypto, String value) {
		return crypto.encrypt(value);
	}

}
