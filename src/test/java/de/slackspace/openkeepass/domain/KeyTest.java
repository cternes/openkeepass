package de.slackspace.openkeepass.domain;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.util.XmlStringCleaner;

public class KeyTest {

    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        Key key = new Key();
        key.setData("someData");

        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(key);

        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<key><Data>someData</Data></key>", xml);
    }
}
