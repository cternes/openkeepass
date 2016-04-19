package de.slackspace.openkeepass.api;

import de.slackspace.openkeepass.domain.KeyFileBytes;

public interface KeyFileParser {

	public KeyFileBytes readKeyFile(byte[] keyFile);
}
