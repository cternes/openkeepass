package de.slackspace.openkeepass.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.processor.NullProtectionStrategy;
import de.slackspace.openkeepass.util.XmlStringCleaner;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class CustomIconsTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(CustomIcons.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
    
    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        UUID uuid = UUID.fromString("87d4f441-a5ec-4ce0-8ca9-82a5079d28ef");
        CustomIcon customIcon = new CustomIconBuilder()
                .uuid(uuid)
                .data(new byte[10])
                .build();
        
        CustomIcons customIcons = new CustomIconsBuilder().addIcon(customIcon).build();
        ByteArrayOutputStream bos = new SimpleXmlParser().toXml(customIcons);
        
        String xml = XmlStringCleaner.cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<customIcons><Icon><UUID>h9T0QaXsTOCMqYKlB50o7w==</UUID><Data>AAAAAAAAAAAAAA==</Data></Icon></customIcons>", xml);
    }
    
    @Test
    public void shouldUnmarshallXmlToObject() throws Exception {
        UUID uuid = UUID.fromString("87d4f441-a5ec-4ce0-8ca9-82a5079d28ef");
        CustomIcon customIcon = new CustomIconBuilder()
                .uuid(uuid)
                .data(new byte[10])
                .build();
        
        CustomIcons customIcons = new CustomIconsBuilder().addIcon(customIcon).build();
        
        String xml = "<customIcons><Icon><UUID>h9T0QaXsTOCMqYKlB50o7w==</UUID><Data>AAAAAAAAAAAAAA==</Data></Icon></customIcons>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        CustomIcons customIconsUnmarshall =
                new SimpleXmlParser().fromXml(inputStream, new NullProtectionStrategy(), CustomIcons.class);
        
        
        Assert.assertEquals(customIcons.getIcons().size(), customIconsUnmarshall.getIcons().size());
        Assert.assertArrayEquals(customIcons.getIcons().get(0).getData(), customIconsUnmarshall.getIcons().get(0).getData());
        Assert.assertEquals(customIcons.getIcons().get(0).getUuid(), customIconsUnmarshall.getIcons().get(0).getUuid());
    }
}
