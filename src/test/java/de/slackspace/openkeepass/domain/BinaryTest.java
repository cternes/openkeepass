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

public class BinaryTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Binary.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        Binary binary = new BinaryBuilder()
                .id(5)
                .data(new byte[10])
                .isCompressed(true)
                .build();

        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(binary);

        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<Binary ID='5' Compressed='True'>H4sIAAAAAAAAAGNggAEAdmiK4woAAAA=</Binary>", xml);
    }

    @Test
    public void shouldUnmarshallXmlToObject() throws Exception {
        Binary binary = new BinaryBuilder()
                .id(5)
                .data(new byte[10])
                .isCompressed(true)
                .build();

        String xml = "<Binary ID='5' Compressed='True'>H4sIAAAAAAAAAGNggAEAdmiK4woAAAA=</Binary>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Binary binaryUnmarshall =
                new SimpleXmlParser().fromXml(inputStream, new NullProtectionStrategy(), Binary.class);

        Assert.assertArrayEquals(binary.getData(), binaryUnmarshall.getData());
        Assert.assertEquals(binary.getId(), binaryUnmarshall.getId());
        Assert.assertEquals(binary.isCompressed(), binaryUnmarshall.isCompressed());
    }
}
