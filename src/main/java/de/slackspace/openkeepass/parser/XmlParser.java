package de.slackspace.openkeepass.parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import de.slackspace.openkeepass.processor.ProtectionStrategy;

public interface XmlParser {

    public <T> T fromXml(InputStream inputStream, ProtectionStrategy decryptionStrategy, Class<T> clazz);

    public ByteArrayOutputStream toXml(Object objectToSerialize);
}
