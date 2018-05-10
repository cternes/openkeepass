package de.slackspace.openkeepass.parser;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.spongycastle.util.encoders.Base64;

import de.slackspace.openkeepass.domain.KeyFile;
import de.slackspace.openkeepass.domain.KeyFileBytes;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException;

public class KeyFileXmlParser implements KeyFileParser {

    private static final String UTF_8 = "UTF-8";
    private static final String MSG_UTF8_NOT_SUPPORTED = "The encoding UTF-8 is not supported";
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
        try {
            return Base64.decode(keyFile.getKey().getData().getBytes(UTF_8));
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(MSG_UTF8_NOT_SUPPORTED, e);
        }
    }
}
