package de.slackspace.openkeepass.domain.enricher;

import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.domain.CustomIcon;
import de.slackspace.openkeepass.domain.CustomIcons;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassFileBuilder;
import de.slackspace.openkeepass.domain.Meta;
import de.slackspace.openkeepass.processor.IconEnricher;

public class IconEnricherTest {

    @Test
    public void shouldAddIconDataAndMaintainIconUuid() {
        UUID iconUuid = UUID.randomUUID();
        byte[] transparentPng = DatatypeConverter
                .parseBase64Binary("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVQYV2NgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII=");

        CustomIcon customIcon = new CustomIcon(iconUuid, transparentPng);
        CustomIcons customIcons = new CustomIcons().addIcon(customIcon);

        Meta meta = new Meta("iconTest").setCustomIcons(customIcons);

        Entry entry1 = new Entry("1").setCustomIconUuid(iconUuid);

        Group groupA = new Group("A").setCustomIconUuid(iconUuid).addEntry(entry1);

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
