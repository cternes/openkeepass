package de.slackspace.openkeepass.domain;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;

public interface KeePassFileElement {

	public ProtectedStringCrypto getProtectedStringCrypto();
}
