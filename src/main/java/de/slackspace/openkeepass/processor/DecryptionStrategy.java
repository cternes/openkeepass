package de.slackspace.openkeepass.processor;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;

public class DecryptionStrategy implements ProtectionStrategy {

	@Override
	public String apply(ProtectedStringCrypto crypto, String value) {
		return crypto.decrypt(value);
	}

}
