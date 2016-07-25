package de.slackspace.openkeepass.parser;

import org.spongycastle.util.encoders.Base64;

import de.slackspace.openkeepass.domain.KeyFileBytes;

public class KeyFileBinaryParser implements KeyFileParser {

    @Override
    public KeyFileBytes readKeyFile(byte[] keyFile) {
        byte[] protectedBuffer = keyFile;

        int length = keyFile.length;
        if (length == 64) {
            protectedBuffer = Base64.decode(keyFile);
        }

        return new KeyFileBytes(true, protectedBuffer);
    }

}
