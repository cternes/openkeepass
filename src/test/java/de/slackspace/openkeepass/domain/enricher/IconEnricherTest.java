package de.slackspace.openkeepass.domain.enricher;

import de.slackspace.openkeepass.domain.*;
import de.slackspace.openkeepass.processor.IconEnricher;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IconEnricherTest {

    @Test
    public void shouldAddIconDataAndMaintainIconUuid() {
        UUID iconUuid = UUID.randomUUID();
        byte[] transparentPng = DatatypeConverter
                .parseBase64Binary("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVQYV2NgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII=");

        CustomIcon customIcon = new CustomIconBuilder().uuid(iconUuid).data(transparentPng).build();

        List<CustomIcon> customIconList = new ArrayList<CustomIcon>();
        customIconList.add(customIcon);

        CustomIcons customIcons = new CustomIconsBuilder().customIcons(customIconList).build();

        Meta meta = new MetaBuilder("iconTest").customIcons(customIcons).build();

        Entry entry1 = new EntryBuilder("1").customIconUuid(iconUuid).build();

        Group groupA = new GroupBuilder("A").customIconUuid(iconUuid).addEntry(entry1).build();

        KeePassFile keePassFile = new KeePassFileBuilder(meta).addTopGroups(groupA).build();
        IconEnricher enricher = new IconEnricher();
        KeePassFile enrichedKeePassFile = enricher.enrichNodesWithIconData(keePassFile);

        Assert.assertEquals("group UUID doesn't match", iconUuid, enrichedKeePassFile.getRoot().getGroups().get(0).getCustomIconUuid());
        Assert.assertArrayEquals("group icon data doesn't match", transparentPng, enrichedKeePassFile.getRoot().getGroups().get(0).getIconData());

        Assert.assertEquals("entry UUID doesn't match", iconUuid, enrichedKeePassFile.getRoot().getGroups().get(0).getEntries().get(0).getCustomIconUuid());
        Assert.assertArrayEquals("entry icon data doesn't match", transparentPng,
                enrichedKeePassFile.getRoot().getGroups().get(0).getEntries().get(0).getIconData());
    }
}
