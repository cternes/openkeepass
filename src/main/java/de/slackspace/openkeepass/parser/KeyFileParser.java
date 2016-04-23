package de.slackspace.openkeepass.parser;

import de.slackspace.openkeepass.domain.KeyFileBytes;

public interface KeyFileParser {

    public KeyFileBytes readKeyFile(byte[] keyFile);
}
