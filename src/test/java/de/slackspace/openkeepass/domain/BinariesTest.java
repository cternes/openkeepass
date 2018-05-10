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

public class BinariesTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Binaries.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        int id = 5;
        Binary binary = new BinaryBuilder()
                .id(id)
                .isCompressed(false)
                .data(new byte[10])
                .build();

        Binaries binaries = new BinariesBuilder().addBinary(binary).build();
        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(binaries);

        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<Binaries><Binary ID='5' Compressed='False'>AAAAAAAAAAAAAA==</Binary></Binaries>", xml);
    }

    @Test
    public void shouldUnmarshallXmlToObject() throws Exception {
        int id = 5;
        Binary binary = new BinaryBuilder()
                .id(id)
                .isCompressed(false)
                .data(new byte[10])
                .build();

        Binaries binaries = new BinariesBuilder().addBinary(binary).build();

        String xml = "<Binaries><Binary ID='5' Compressed='False'>AAAAAAAAAAAAAA==</Binary></Binaries>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Binaries binariesUnmarshall =
                new SimpleXmlParser().fromXml(inputStream, new NullProtectionStrategy(), Binaries.class);

        Assert.assertEquals(binaries.getBinaries().size(), binariesUnmarshall.getBinaries().size());
        Assert.assertArrayEquals(binaries.getBinaries().get(0).getData(), binariesUnmarshall.getBinaries().get(0).getData());
        Assert.assertEquals(binaries.getBinaries().get(0).getId(), binariesUnmarshall.getBinaries().get(0).getId());
        Assert.assertEquals(binaries.getBinaries().get(0).isCompressed(), binariesUnmarshall.getBinaries().get(0).isCompressed());
    }
}
