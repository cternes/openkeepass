package de.slackspace.openkeepass.processor;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;

import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.Property;
import de.slackspace.openkeepass.domain.zipper.GroupZipper;
import de.slackspace.openkeepass.util.StringUtils;

public class ReferencesEnricher {

    /**
     * Iterates through all nodes of the given KeePass file and replace the nodes with enriched attachment data nodes.
     *
     * @param keePassFile the KeePass file which should be iterated
     * @return an enriched KeePass file
     */
    public KeePassFile enrichNodesWithReferences(KeePassFile keePassFile) {
        GroupZipper zipper = new GroupZipper(keePassFile);
        Iterator<Group> iter = zipper.iterator();

        while (iter.hasNext()) {
            Group group = iter.next();

            enrichEntriesWithReferences(group, keePassFile);
        }

        return zipper.close();
    }

    private void enrichEntriesWithReferences(Group group, KeePassFile keePassFile) {
        List<Entry> entries = group.getEntries();
        for (Entry entry : entries) {
            for (Property property : entry.getProperties()) {
                String value = property.getValue();

                if (isUUIDReference(value)) {
                    UUID uuid = getUUID(value);
                    Entry referenceEntry = keePassFile.getEntryByUUID(uuid);

                    if (referenceEntry != null) {
                        String referenceValue = getValueFromReferenceEntry(value, referenceEntry);
                        entry.getReferencedProperties().add(new Property(property.getKey(), referenceValue, false));
                    }
                }
            }
        }
    }

    private String getValueFromReferenceEntry(String reference, Entry referenceEntry) {
        String field = getReferenceField(reference);

        if ("A".equals(field)) {
            return referenceEntry.getUrl();
        }
        else if ("P".equals(field)) {
            return referenceEntry.getPassword();
        }
        else if ("U".equals(field)) {
            return referenceEntry.getUsername();
        }
        else if ("N".equals(field)) {
            return referenceEntry.getNotes();
        }
        else if ("T".equals(field)) {
            return referenceEntry.getTitle();
        }
        else {
            return reference;
        }
    }

    private boolean isUUIDReference(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        return Entry.REFERENCE_PATTERN.matcher(value).matches();
    }

    private UUID getUUID(String value) {
        Matcher matcher = Entry.REFERENCE_PATTERN.matcher(value);
        matcher.find();
        String uuidString = StringUtils.convertToUUIDString(matcher.group(2));

        return UUID.fromString(uuidString);
    }

    private String getReferenceField(String value) {
        Matcher matcher = Entry.REFERENCE_PATTERN.matcher(value);
        matcher.find();
        return matcher.group(1);
    }
}
