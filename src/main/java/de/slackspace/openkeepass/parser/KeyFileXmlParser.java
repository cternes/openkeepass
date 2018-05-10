package de.slackspace.openkeepass.parser;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.spongycastle.util.encoders.Base64;

import de.slackspace.openkeepass.domain.KeyFile;
import de.slackspace.openkeepass.domain.KeyFileBytes;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException;

public class KeyFileXmlParser implements KeyFileParser {

    private final XmlParser parser;
    
    public KeyFileXmlParser(XmlParser parser) {
        this.parser = parser;
    }
    
    @Override
    public KeyFileBytes readKeyFile(byte[] keyFile) {
        KeyFile xmlKeyFile = fromXml(keyFile);

        byte[] protectedBuffer = null;

        if (xmlKeyFile.isXmlFile()) {
            protectedBuffer = getBytesFromKeyFile(xmlKeyFile);
        }

        return new KeyFileBytes(xmlKeyFile.isXmlFile(), protectedBuffer);
    }

    public KeyFile fromXml(byte[] inputBytes) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
            return parser.fromXml(inputStream, null, KeyFile.class);
        } catch (KeePassDatabaseUnreadableException e) {
            return new KeyFile(false);
        }
    }

    private byte[] getBytesFromKeyFile(KeyFile keyFile) {
        return Base64.decode(keyFile.getKey().getData().getBytes(StandardCharsets.UTF_8));
    }
}
