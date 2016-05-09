package de.slackspace.openkeepass.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import de.slackspace.openkeepass.domain.CustomIcon;
import de.slackspace.openkeepass.domain.CustomIcons;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.EntryBuilder;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.GroupBuilder;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.zipper.GroupZipper;
import de.slackspace.openkeepass.exception.IconUnreadableException;
import de.slackspace.openkeepass.util.StreamUtils;

/**
 * Adds the raw icon data of all nodes in a KeePass file to the nodes itself.
 * This makes it possible to call {@link Group#getIconData()} and
 * {@link Entry#getIconData()} on the nodes to retrieve the icon data.
 *
 * @see Group#getIconData()
 * @see Entry#getIconData()
 */
public class IconEnricher {

    private static final String PNG = ".png";
    private static final String ICONS = "/icons/";

    /**
     * Iterates through all nodes of the given KeePass file and replace the
     * nodes with enriched icon data nodes.
     *
     * @param keePassFile
     *            the KeePass file which should be iterated
     * @return an enriched KeePass file
     */
    public KeePassFile enrichNodesWithIconData(KeePassFile keePassFile) {
        CustomIcons iconLibrary = keePassFile.getMeta().getCustomIcons();
        GroupZipper zipper = new GroupZipper(keePassFile);
        Iterator<Group> iter = zipper.iterator();

        while (iter.hasNext()) {
            Group group = iter.next();

            byte[] iconData = getIconData(group.getCustomIconUuid(), group.getIconId(), iconLibrary);
            Group groupWithIcon = new GroupBuilder(group).iconData(iconData).build();
            zipper.replace(groupWithIcon);

            enrichEntriesWithIcons(iconLibrary, group);
        }

        return zipper.close();
    }

    private void enrichEntriesWithIcons(CustomIcons iconLibrary, Group group) {
        List<Entry> removeList = new ArrayList<Entry>();
        List<Entry> addList = new ArrayList<Entry>();

        List<Entry> entries = group.getEntries();
        for (Entry entry : entries) {
            byte[] entryIconData = getIconData(entry.getCustomIconUuid(), entry.getIconId(), iconLibrary);
            Entry entryWithIcon = new EntryBuilder(entry).iconData(entryIconData).build();

            removeList.add(entry);
            addList.add(entryWithIcon);
        }

        group.getEntries().removeAll(removeList);
        group.getEntries().addAll(addList);
    }

    private byte[] getIconData(UUID customIconUuid, int stockIconId, CustomIcons iconLibrary) {
        byte[] iconData;

        if (customIconUuid != null) {
            CustomIcon icon = iconLibrary.getIconByUuid(customIconUuid);
            iconData = icon.getData();
        } else {
            iconData = getStockIconData(stockIconId);
        }

        return iconData;
    }

    private byte[] getStockIconData(int iconId) {
        if (iconId < 0) {
            return null;
        }

        InputStream inputStream = getClass().getResourceAsStream(ICONS + iconId + PNG);
        if (inputStream == null) {
            return null;
        }

        try {
            return StreamUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new IconUnreadableException("Could not read icon data from resource '" + ICONS + iconId + PNG + "'", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
}
