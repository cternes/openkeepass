package de.slackspace.openkeepass.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.slackspace.openkeepass.domain.Attachment;
import de.slackspace.openkeepass.domain.Binaries;
import de.slackspace.openkeepass.domain.Binary;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.EntryBuilder;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.zipper.GroupZipper;

/**
 * Adds the raw attachment data of all nodes in a KeePass file to the nodes
 * itself. This makes it possible to call {@link Attachment#getData()} and on
 * the nodes to retrieve the icon data.
 *
 * @see Attachment#getData()
 */
public class BinaryEnricher {

    /**
     * Iterates through all nodes of the given KeePass file and replace the
     * nodes with enriched attachment data nodes.
     *
     * @param keePassFile
     *            the KeePass file which should be iterated
     * @return an enriched KeePass file
     */
    public KeePassFile enrichNodesWithBinaryData(KeePassFile keePassFile) {
        Binaries binaryLibrary = keePassFile.getMeta().getBinaries();
        GroupZipper zipper = new GroupZipper(keePassFile);
        Iterator<Group> iter = zipper.iterator();

        while (iter.hasNext()) {
            Group group = iter.next();

            enrichEntriesWithBinaryData(binaryLibrary, group);
        }

        return zipper.close();
    }

    private void enrichEntriesWithBinaryData(Binaries binaryLibrary, Group group) {
        ArrayList<Entry> removeList = new ArrayList<Entry>();
        ArrayList<Entry> addList = new ArrayList<Entry>();

        List<Entry> entries = group.getEntries();
        for (Entry entry : entries) {
            EntryBuilder newEntryBuilder = new EntryBuilder(entry);
            List<Attachment> newAttachmentList = newEntryBuilder.getAttachmentList();
            newAttachmentList.clear();

            for (Attachment attachment : entry.getAttachments()) {
                byte[] attachmentData = getBinaryData(attachment.getRef(), binaryLibrary);
                Attachment attachmentWithData = new Attachment(attachment.getKey(), attachment.getRef(), attachmentData);
                newAttachmentList.add(attachmentWithData);
            }

            Entry entryWithAttachmentData = newEntryBuilder.build();

            removeList.add(entry);
            addList.add(entryWithAttachmentData);
        }

        group.getEntries().removeAll(removeList);
        group.getEntries().addAll(addList);
    }

    private byte[] getBinaryData(int binaryId, Binaries binaryLibrary) {
        Binary binary = binaryLibrary.getBinaryById(binaryId);
        return binary.getData();
    }
}
