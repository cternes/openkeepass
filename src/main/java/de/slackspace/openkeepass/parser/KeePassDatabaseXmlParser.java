package de.slackspace.openkeepass.parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.bind.JAXB;

import de.slackspace.openkeepass.domain.KeePassFile;

public class KeePassDatabaseXmlParser {

    public KeePassFile fromXml(InputStream inputStream) {
        return JAXB.unmarshal(inputStream, KeePassFile.class);
    }

    public ByteArrayOutputStream toXml(KeePassFile keePassFile) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JAXB.marshal(keePassFile, outputStream);

        return outputStream;
    }
}
