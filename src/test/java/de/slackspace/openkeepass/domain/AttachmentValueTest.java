package de.slackspace.openkeepass.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.processor.NullProtectionStrategy;
import de.slackspace.openkeepass.util.XmlStringCleaner;

public class AttachmentValueTest {

    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        AttachmentValue attachmentValue = new AttachmentValue(3);

        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(attachmentValue);

        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<Value Ref='3'/>", xml);
    }

    @Test
    public void shouldUnmarshallXmlToObject() throws Exception {
        AttachmentValue attachmentValue = new AttachmentValue(3);

        String xml = "<Value Ref='3' />";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        AttachmentValue attachmentValueUnmarshalled =
                new SimpleXmlParser().fromXml(inputStream, new NullProtectionStrategy(), AttachmentValue.class);

        Assert.assertEquals(attachmentValue.getRef(), attachmentValueUnmarshalled.getRef());
    }

}
