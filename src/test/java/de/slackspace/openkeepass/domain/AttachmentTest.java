package de.slackspace.openkeepass.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.processor.NullProtectionStrategy;
import de.slackspace.openkeepass.util.XmlStringCleaner;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class AttachmentTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Attachment.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        Attachment attachment = new Attachment("test.txt", 3);
        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(attachment);

        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<Binary><Key>test.txt</Key><Value Ref='3'/></Binary>", xml);
    }

    @Test
    public void shouldUnmarshallXmlToObject() throws Exception {
        Attachment attachment = new Attachment("test.txt", 3);

        String xml = "<Binary><Key>test.txt</Key><Value Ref='3'/></Binary>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Attachment attachmentUnmarshalled =
                new SimpleXmlParser().fromXml(inputStream, new NullProtectionStrategy(), Attachment.class);

        Assert.assertEquals(attachment.getKey(), attachmentUnmarshalled.getKey());
        Assert.assertEquals(attachment.getRef(), attachmentUnmarshalled.getRef());
    }
}
