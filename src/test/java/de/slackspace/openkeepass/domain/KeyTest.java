package de.slackspace.openkeepass.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleV3XmlParser;
import de.slackspace.openkeepass.util.XmlStringCleaner;

public class KeyTest {

    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        Key key = new Key();
        key.setData("someData");

        ByteArrayOutputStream bos = new SimpleV3XmlParser().toXml(key);

        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<Key><Data>someData</Data></Key>", xml);
    }
    
    @Test
    public void shouldUnmarshallObject() {
        String xml = "<Key><Data>someData</Data></Key>";
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Key key = new SimpleV3XmlParser().fromXml(inputStream, Key.class);

        Assert.assertEquals("someData", key.getData());
    }
}
