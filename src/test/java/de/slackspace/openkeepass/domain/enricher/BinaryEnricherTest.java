package de.slackspace.openkeepass.domain.enricher;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Test;
import org.spongycastle.pqc.math.linearalgebra.ByteUtils;

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

public class BinaryEnricherTest {

    @Test
    public void shouldAddAttachmentDataAndMaintainBinaryId() {
        int attachmentId = 5;
        String attachmentKey = "test.txt";
        byte[] attachmentData = ByteUtils.fromHexString("FF00EE");

        Binary binary = new BinaryBuilder().id(attachmentId).isCompressed(false).data(attachmentData).build();

        ArrayList<Binary> binaryList = new ArrayList<Binary>();
        binaryList.add(binary);

        Binaries binaries = new BinariesBuilder().binaries(binaryList).build();

        Meta meta = new MetaBuilder("binaryTest").binaries(binaries).build();

        Entry entry1 = new EntryBuilder("1").addAttachment(attachmentKey, attachmentId).build();

        Group groupA = new GroupBuilder("A").addEntry(entry1).build();

        KeePassFile keePassFile = new KeePassFileBuilder(meta).addTopGroups(groupA).build();
        BinaryEnricher enricher = new BinaryEnricher();
        KeePassFile enrichedKeePassFile = enricher.enrichNodesWithBinaryData(keePassFile);

        Attachment attachment =
                enrichedKeePassFile.getRoot().getGroups().get(0).getEntries().get(0).getAttachments().get(0);

        assertThat(attachment.getRef(), is(attachmentId));
        assertThat(attachment.getKey(), is(attachmentKey));
        assertThat(attachment.getData(), is(attachmentData));
    }
}
