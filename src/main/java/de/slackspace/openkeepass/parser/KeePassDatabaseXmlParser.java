package de.slackspace.openkeepass.parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import de.slackspace.openkeepass.domain.KeePassFile;

public class KeePassDatabaseXmlParser {

    private XmlParser parser;
    
    public KeePassDatabaseXmlParser(XmlParser parser) {
        this.parser = parser;
    }
    
    public KeePassFile fromXml(InputStream inputStream) {
        return parser.fromXml(inputStream, KeePassFile.class);
    }

    public ByteArrayOutputStream toXml(KeePassFile keePassFile) {
        return parser.toXml(keePassFile);
    }
}
