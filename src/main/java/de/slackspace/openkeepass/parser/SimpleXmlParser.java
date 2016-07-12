package de.slackspace.openkeepass.parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.GregorianCalendar;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;

import de.slackspace.openkeepass.domain.xml.adapter.BooleanSimpleXmlAdapter;
import de.slackspace.openkeepass.domain.xml.adapter.CalendarSimpleXmlAdapter;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnwriteableException;

public class SimpleXmlParser implements XmlParser {

    public Object fromXml(InputStream inputStream, Class<?> clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    public ByteArrayOutputStream toXml(Object objectToSerialize) {
        try {
            RegistryMatcher matcher = new RegistryMatcher();
            matcher.bind(Boolean.class, BooleanSimpleXmlAdapter.class);
            matcher.bind(GregorianCalendar.class, CalendarSimpleXmlAdapter.class);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Serializer serializer = new Persister(matcher);
            serializer.write(objectToSerialize, outputStream);
            return outputStream;
        }
        catch (Exception e) {
            throw new KeePassDatabaseUnwriteableException("Could not serialize object to xml", e);
        }
    }

}
