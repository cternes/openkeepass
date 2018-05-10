package de.slackspace.openkeepass.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.processor.NullProtectionStrategy;
import de.slackspace.openkeepass.util.XmlStringCleaner;

public class KeyFileTest {

    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        Key key = new Key();
        key.setData("someData");
        KeyFile keyFile = new KeyFile(true, key);
      
        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(keyFile);

        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<KeyFile><Key><Data>someData</Data></Key></KeyFile>", xml);
    }
    
    @Test
    public void shouldUnmarshallObject() {
        String xml = "<KeyFile><Key><Data>RP+rYNZL4lrGtDMBPzOuctlh3NAutSG5KGsT38C+qPQ=</Data></Key></KeyFile>";
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        KeyFile keyFile = new SimpleXmlParser().fromXml(inputStream, new NullProtectionStrategy(), KeyFile.class);

        Assert.assertEquals("RP+rYNZL4lrGtDMBPzOuctlh3NAutSG5KGsT38C+qPQ=", keyFile.getKey().getData());
    }
}
