package de.slackspace.openkeepass.parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface XmlParser {

    public Object fromXml(InputStream inputStream, Class<?> clazz);

    public ByteArrayOutputStream toXml(Object objectToSerialize);
}
