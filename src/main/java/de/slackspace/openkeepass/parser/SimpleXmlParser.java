package de.slackspace.openkeepass.parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.transform.RegistryMatcher;

import de.slackspace.openkeepass.domain.PropertyValue;
import de.slackspace.openkeepass.domain.xml.adapter.BooleanSimpleXmlAdapter;
import de.slackspace.openkeepass.domain.xml.adapter.ByteSimpleXmlAdapter;
import de.slackspace.openkeepass.domain.xml.adapter.CalendarSimpleXmlAdapter;
import de.slackspace.openkeepass.domain.xml.adapter.PropertyValueXmlAdapter;
import de.slackspace.openkeepass.domain.xml.adapter.TreeStrategyWithoutArrayLength;
import de.slackspace.openkeepass.domain.xml.adapter.UUIDSimpleXmlAdapter;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnwriteableException;
import de.slackspace.openkeepass.processor.ProtectionStrategy;

public class SimpleXmlParser implements XmlParser {

    public <T> T fromXml(InputStream inputStream, ProtectionStrategy protectionStrategy, Class<T> clazz) {
        try {
            Serializer serializer = createSerializer(protectionStrategy);
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

    private Serializer createSerializer(ProtectionStrategy protectionStrategy) {
        RegistryMatcher matcher = new RegistryMatcher();
        matcher.bind(Boolean.class, BooleanSimpleXmlAdapter.class);
        matcher.bind(GregorianCalendar.class, CalendarSimpleXmlAdapter.class);
        matcher.bind(UUID.class, UUIDSimpleXmlAdapter.class);
        matcher.bind(byte[].class, ByteSimpleXmlAdapter.class);
        
        Registry registry = new Registry();
        PropertyValueXmlAdapter converter = new PropertyValueXmlAdapter(protectionStrategy);
        try {
            registry.bind(PropertyValue.class, converter);
            Strategy strategy = new RegistryStrategy(registry);
            return new Persister(strategy, matcher);
        }
        catch (Exception e) {
            throw new KeePassDatabaseUnreadableException("Could not register xml converter", e);
        }
    }
}
