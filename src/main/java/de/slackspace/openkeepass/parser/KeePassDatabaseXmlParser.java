package de.slackspace.openkeepass.parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.processor.ProtectionStrategy;

public class KeePassDatabaseXmlParser {

    private XmlParser parser;
    
    public KeePassDatabaseXmlParser(XmlParser parser) {
        this.parser = parser;
    }
    
    public KeePassFile fromXml(InputStream inputStream, ProtectionStrategy protectionStrategy) {
        return parser.fromXml(inputStream, protectionStrategy, KeePassFile.class);
    }

    public ByteArrayOutputStream toXml(KeePassFile keePassFile) {
        return parser.toXml(keePassFile);
    }
}
