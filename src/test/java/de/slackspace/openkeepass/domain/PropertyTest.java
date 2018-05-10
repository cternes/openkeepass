package de.slackspace.openkeepass.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleV3XmlParser;
import de.slackspace.openkeepass.util.XmlStringCleaner;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class PropertyTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Property.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
    
    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        Property property = new Property("SomeKey", "SomeValue", false);
        ByteArrayOutputStream bos = new SimpleV3XmlParser().toXml(property);
        
        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<String><Key>SomeKey</Key><Value Protected='False'>SomeValue</Value></String>", xml);
    }
    
    @Test
    public void shouldUnmarshallXmlToObject() throws Exception {
        Property property = new Property("SomeKey", "SomeValue", false);

        String xml = "<String><Key>SomeKey</Key><Value Protected='False'>SomeValue</Value></String>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Property propertyUnmarshalled = new SimpleV3XmlParser().fromXml(inputStream, Property.class);

        Assert.assertEquals(property.getKey(), propertyUnmarshalled.getKey());
        Assert.assertEquals(property.getValue(), propertyUnmarshalled.getValue());
    }
}
