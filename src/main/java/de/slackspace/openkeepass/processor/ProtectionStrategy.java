package de.slackspace.openkeepass.processor;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;

public interface ProtectionStrategy {

	public String apply(ProtectedStringCrypto crypto, String value);
}
