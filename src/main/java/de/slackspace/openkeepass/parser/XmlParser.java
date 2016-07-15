package de.slackspace.openkeepass.parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface XmlParser {

    public <T> T fromXml(InputStream inputStream, Class<T> clazz);

    public ByteArrayOutputStream toXml(Object objectToSerialize);
}
