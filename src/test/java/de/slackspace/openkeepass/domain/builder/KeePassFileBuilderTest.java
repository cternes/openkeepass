package de.slackspace.openkeepass.domain.builder;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.domain.EntryBuilder;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.GroupBuilder;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassFileBuilder;

public class KeePassFileBuilderTest {

    @Test
    public void shouldBuildKeePassFileWithReasonableDefaults() {
        KeePassFile keePassFile = new KeePassFileBuilder("testDB").build();

        Assert.assertEquals("testDB", keePassFile.getMeta().getDatabaseName());
        Assert.assertEquals("KeePass", keePassFile.getMeta().getGenerator());
        Assert.assertNotNull(keePassFile.getRoot());
        Assert.assertEquals(1, keePassFile.getRoot().getGroups().size());
    }

    @Test
    public void shouldBuildKeePassFileWithTreeStructure() {
        /*
         * Should create the following structure:
         * 
         * Root | |-- First entry (E) |-- Banking (G) | |-- Internet (G) | |--
         * Shopping (G) |-- Second entry (E)
         * 
         */
        Group root = new GroupBuilder().addEntry(new EntryBuilder("First entry").build()).addGroup(new GroupBuilder("Banking").build())
                .addGroup(
                        new GroupBuilder("Internet").addGroup(new GroupBuilder("Shopping").addEntry(new EntryBuilder("Second entry").build()).build()).build())
                .build();

        KeePassFile keePassFile = new KeePassFileBuilder("writeTreeDB").addTopGroups(root).build();

        Assert.assertEquals("Banking", keePassFile.getTopGroups().get(0).getName());
        Assert.assertEquals("Internet", keePassFile.getTopGroups().get(1).getName());
        Assert.assertEquals("First entry", keePassFile.getTopEntries().get(0).getTitle());
        Assert.assertEquals("Shopping", keePassFile.getTopGroups().get(1).getGroups().get(0).getName());
        Assert.assertEquals("Second entry", keePassFile.getTopGroups().get(1).getGroups().get(0).getEntries().get(0).getTitle());
        Assert.assertNotNull(keePassFile.getEntryByTitle("Second entry"));
    }
}
