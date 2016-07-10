package de.slackspace.openkeepass.domain;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.util.XmlStringCleaner;

public class PropertyValueTest {

    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        PropertyValue propertyValue = new PropertyValue(false, "TestValue");
        
        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(propertyValue);
        
        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<propertyValue Protected='False'>TestValue</propertyValue>", xml);
    }
    
}
