package de.slackspace.openkeepass.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.slackspace.openkeepass.crypto.Sha256;
import de.slackspace.openkeepass.domain.KeyFileBytes;
import de.slackspace.openkeepass.exception.KeyFileUnreadableException;
import de.slackspace.openkeepass.parser.KeyFileBinaryParser;
import de.slackspace.openkeepass.parser.KeyFileParser;
import de.slackspace.openkeepass.parser.KeyFileXmlParser;
import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.util.StreamUtils;

public class KeyFileReader {

    protected List<KeyFileParser> keyFileParsers = new ArrayList<KeyFileParser>();

    public KeyFileReader() {
        keyFileParsers.add(new KeyFileXmlParser(new SimpleXmlParser()));
        keyFileParsers.add(new KeyFileBinaryParser());
    }

    public byte[] readKeyFile(InputStream keyFileStream) {
        byte[] keyFileContent = toByteArray(keyFileStream);

        byte[] protectedBuffer = parseKeyFile(keyFileContent);
        return hashKeyFileIfNecessary(protectedBuffer);
    }

    private byte[] parseKeyFile(byte[] keyFileContent) {
        for (KeyFileParser parser : keyFileParsers) {
            KeyFileBytes keyFileBytes = parser.readKeyFile(keyFileContent);

            if (keyFileBytes.isReadable()) {
                return keyFileBytes.getBytes();
            }
        }

        throw new KeyFileUnreadableException("Could not parse key file because no parser was able to parse the file");
    }

    private byte[] toByteArray(InputStream keyFileStream) {
        try {
            return StreamUtils.toByteArray(keyFileStream);
        } catch (IOException e) {
            throw new KeyFileUnreadableException("Could not read key file", e);
        }
    }

    private byte[] hashKeyFileIfNecessary(byte[] protectedBuffer) {
        if (protectedBuffer.length != 32) {
            return Sha256.hash(protectedBuffer);
        }

        return protectedBuffer;
    }
}
