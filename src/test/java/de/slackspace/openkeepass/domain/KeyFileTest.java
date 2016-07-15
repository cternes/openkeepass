package de.slackspace.openkeepass.domain;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.util.XmlStringCleaner;

public class KeyFileTest {

    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        Key key = new Key();
        key.setData("someData");
        KeyFile keyFile = new KeyFile(true, key);
      
        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(keyFile);

        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<keyFile><Key><Data>someData</Data></Key></keyFile>", xml);
    }
}
