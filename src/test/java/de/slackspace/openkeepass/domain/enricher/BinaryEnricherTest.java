package de.slackspace.openkeepass.domain.enricher;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;

import de.slackspace.openkeepass.domain.Attachment;
import de.slackspace.openkeepass.domain.Binaries;
import de.slackspace.openkeepass.domain.BinariesBuilder;
import de.slackspace.openkeepass.domain.Binary;
import de.slackspace.openkeepass.domain.BinaryBuilder;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.EntryBuilder;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.GroupBuilder;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassFileBuilder;
import de.slackspace.openkeepass.domain.Meta;
import de.slackspace.openkeepass.domain.MetaBuilder;
import de.slackspace.openkeepass.processor.BinaryEnricher;
import org.junit.Assert;
import org.junit.Test;

public class BinaryEnricherTest {

    @Test
    public void shouldAddAttachmentDataAndMaintainBinaryId() {
        int attachmentId = 5;
        String attachmentKey = "test.txt";
        byte[] attachmentData = DatatypeConverter.parseBase64Binary("H4sIAAAAAAAAAwtJrSgBAPkIuZsEAAAA");

        Binary binary = new BinaryBuilder().id(attachmentId).isCompressed(true).data(attachmentData).build();

        ArrayList<Binary> binaryList = new ArrayList<Binary>();
        binaryList.add(binary);

        Binaries binaries = new BinariesBuilder().binaries(binaryList).build();

        Meta meta = new MetaBuilder("binaryTest").binaries(binaries).build();

        Entry entry1 = new EntryBuilder("1").addAttachment(attachmentKey, attachmentId).build();

        Group groupA = new GroupBuilder("A").addEntry(entry1).build();

        KeePassFile keePassFile = new KeePassFileBuilder(meta).addTopGroups(groupA).build();
        BinaryEnricher enricher = new BinaryEnricher();
        KeePassFile enrichedKeePassFile = enricher.enrichNodesWithBinaryData(keePassFile);

        Attachment attachment = enrichedKeePassFile.getRoot().getGroups().get(0).getEntries().get(0).getAttachments().get(0);
        Assert.assertEquals("attachment id doesn't match", attachmentId, attachment.getRef());
        Assert.assertEquals("attachment key doesn't match", attachmentKey, attachment.getKey());
        Assert.assertEquals("attachment data doesn't match", attachmentData, attachment.getData());
    }
}
