package de.slackspace.openkeepass.parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;

import de.slackspace.openkeepass.domain.xml.adapter.BooleanSimpleXmlAdapter;
import de.slackspace.openkeepass.domain.xml.adapter.ByteSimpleXmlAdapter;
import de.slackspace.openkeepass.domain.xml.adapter.CalendarSimpleXmlAdapter;
import de.slackspace.openkeepass.domain.xml.adapter.TreeStrategyWithoutArrayLength;
import de.slackspace.openkeepass.domain.xml.adapter.UUIDSimpleXmlAdapter;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnwriteableException;

public class SimpleXmlParser implements XmlParser {

    public <T> T fromXml(InputStream inputStream, Class<T> clazz) {
        try {
            Serializer serializer = createSerializer();
            return serializer.read(clazz, inputStream);
        }
        catch (Exception e) {
            throw new KeePassDatabaseUnreadableException("Could not deserialize object to xml", e);
        }
    }

    public ByteArrayOutputStream toXml(Object objectToSerialize) {
        try {
            RegistryMatcher matcher = new RegistryMatcher();
            matcher.bind(Boolean.class, BooleanSimpleXmlAdapter.class);
            matcher.bind(GregorianCalendar.class, CalendarSimpleXmlAdapter.class);
            matcher.bind(UUID.class, UUIDSimpleXmlAdapter.class);
            matcher.bind(byte[].class, ByteSimpleXmlAdapter.class);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TreeStrategyWithoutArrayLength strategy = new TreeStrategyWithoutArrayLength();
            Serializer serializer = new Persister(strategy, matcher);
            serializer.write(objectToSerialize, outputStream);
            return outputStream;
        }
        catch (Exception e) {
            throw new KeePassDatabaseUnwriteableException("Could not serialize object to xml", e);
        }
    }

    private Serializer createSerializer() {
        RegistryMatcher matcher = new RegistryMatcher();
        matcher.bind(Boolean.class, BooleanSimpleXmlAdapter.class);
        matcher.bind(GregorianCalendar.class, CalendarSimpleXmlAdapter.class);
        matcher.bind(UUID.class, UUIDSimpleXmlAdapter.class);
        matcher.bind(byte[].class, ByteSimpleXmlAdapter.class);
        
        TreeStrategyWithoutArrayLength strategy = new TreeStrategyWithoutArrayLength();
        Serializer serializer = new Persister(strategy, matcher);
        return serializer;
    }
}
