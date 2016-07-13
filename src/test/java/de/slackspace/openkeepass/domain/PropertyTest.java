package de.slackspace.openkeepass.domain;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleXmlParser;
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
        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(property);
        
        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<String><Key>SomeKey</Key><Value Protected='False'>SomeValue</Value></String>", xml);
    }
}
