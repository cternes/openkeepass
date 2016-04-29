package de.slackspace.openkeepass.domain.zipper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.EntryBuilder;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.GroupBuilder;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassFileBuilder;

public class GroupZipperTest {

    @Test
    public void shouldNavigateThroughTreeAndReplaceGroupAndEntryNode() {
        KeePassFile keePassFile = createTreeStructure();

        GroupZipper zipper = new GroupZipper(keePassFile).down().right().down();
        Group shoppingGroup = zipper.getNode();
        Entry shoppingEntry = shoppingGroup.getEntryByTitle("Second entry");
        Group modifiedGroup = new GroupBuilder(shoppingGroup).name("Fashion").removeEntry(shoppingEntry)
                .addEntry(new EntryBuilder(shoppingEntry).title("Entry #2").build()).build();

        zipper = zipper.replace(modifiedGroup);

        KeePassFile keePassFileModified = zipper.close();
        Assert.assertNotNull(keePassFileModified.getGroupByName("Fashion"));
        Assert.assertNotNull(keePassFileModified.getEntryByTitle("Entry #2"));
    }

    private KeePassFile createTreeStructure() {
        /*
         * Should create the following structure:
         *
         * Root | |-- First entry (E) |-- Banking (G) | |-- Internet (G) | |--
         * Shopping (G) |-- Second entry (E) | |-- Stores (G) | |-- Music (G)
         */
        Group root = new GroupBuilder().addEntry(new EntryBuilder("First entry").build()).addGroup(new GroupBuilder("Banking").build())
                .addGroup(new GroupBuilder("Internet").addGroup(new GroupBuilder("Shopping").addEntry(new EntryBuilder("Second entry").build()).build())
                        .addGroup(new GroupBuilder("Stores").build()).build())
                .addGroup(new GroupBuilder("Music").build()).build();

        KeePassFile keePassFile = new KeePassFileBuilder("writeTreeDB").addTopGroups(root).build();
        return keePassFile;
    }

    @Test
    public void shouldNavigateThroughTreeLeftAndRightWithoutProblems() {
        KeePassFile keePassFile = createFlatGroupStructure();

        GroupZipper zipper = new GroupZipper(keePassFile);
        Assert.assertEquals("C", zipper.down().right().right().getNode().getName());
        Assert.assertEquals("A", zipper.left().left().getNode().getName());
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowExeptionWhileMovingTooFarRight() {
        KeePassFile keePassFile = createFlatGroupStructure();

        GroupZipper zipper = new GroupZipper(keePassFile).down().right().right();

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Could not move right because the last node at this level has already been reached");
        zipper.right();
    }

    @Test
    public void shouldThrowExeptionWhileMovingTooFarLeft() {
        KeePassFile keePassFile = createFlatGroupStructure();

        GroupZipper zipper = new GroupZipper(keePassFile).down();

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Could not move left because the first node at this level has already been reached");
        zipper.left();
    }

    @Test
    public void shouldThrowExeptionWhileMovingDownWithoutChilds() {
        KeePassFile keePassFile = createFlatGroupStructure();

        GroupZipper zipper = new GroupZipper(keePassFile).down();

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Could not move down because this group does not have any children");
        zipper.down();
    }

    @Test
    public void shouldThrowExeptionWhileMovingUpWithoutParent() {
        KeePassFile keePassFile = createFlatGroupStructure();

        GroupZipper zipper = new GroupZipper(keePassFile);

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Could not move up because this group does not have a parent");
        zipper.up();
    }

    @Test
    public void shouldIterateThroughAllGroups() {
        KeePassFile keePassFile = createTreeStructure();

        GroupZipper zipper = new GroupZipper(keePassFile);
        Iterator<Group> iter = zipper.iterator();

        List<Group> visitedGroups = new ArrayList<Group>();
        while (iter.hasNext()) {
            Group group = iter.next();
            visitedGroups.add(group);
        }

        Assert.assertEquals(6, visitedGroups.size());
    }

    @Test
    public void shouldVisitAllDeeplyNestedNodes() {
        Group rootA = new GroupBuilder("A")
                .addGroup(
                        new GroupBuilder("B")
                        .addGroup(
                                new GroupBuilder("C")
                                .addGroup(
                                        new GroupBuilder("D")
                                        .build())
                                .addGroup(new GroupBuilder("E").build()).build())
                        .addGroup(new GroupBuilder("F").build())
                        .addGroup(
                                new GroupBuilder("G")
                                .addGroup(new GroupBuilder("H").addGroup(new GroupBuilder("I").addGroup(new GroupBuilder("J").build()).build())
                                        .addGroup(
                                                new GroupBuilder("K").build())
                                        .build())
                                .addGroup(new GroupBuilder("L").build()).build())
                        .build())
                .addGroup(new GroupBuilder("M").build())
                .addGroup(new GroupBuilder("N")
                        .addGroup(new GroupBuilder("O").addGroup(new GroupBuilder("P").addGroup(new GroupBuilder("Q").build()).build()).build())
                        .addGroup(new GroupBuilder("R").build()).addGroup(new GroupBuilder("S").addGroup(new GroupBuilder("T").build()).build()).build())
                .build();

        KeePassFile db = new KeePassFileBuilder("deepTest").addTopGroups(rootA).build();
        GroupZipper zipper = new GroupZipper(db);
        Iterator<Group> iterator = zipper.iterator();
        StringBuilder sb = new StringBuilder();

        while (iterator.hasNext()) {
            Group next = iterator.next();
            sb.append(next.getName());
        }

        Assert.assertEquals("ABCDEFGHIJKLMNOPQRST", sb.toString());
    }

    @Test
    public void shouldCreateCloneFromTreeStructure() {
        KeePassFile keePassFile = createTreeStructure();
        KeePassFile clonedKeePassFile = new GroupZipper(keePassFile).cloneKeePassFile();

        compareKeePassFiles(keePassFile, clonedKeePassFile);
    }

    @Test
    public void shouldCreateCloneFromFlatStructure() {
        KeePassFile keePassFile = createFlatGroupStructure();
        KeePassFile clonedKeePassFile = new GroupZipper(keePassFile).cloneKeePassFile();

        compareKeePassFiles(keePassFile, clonedKeePassFile);
    }

    private void compareKeePassFiles(KeePassFile keePassFile, KeePassFile clonedKeePassFile) {
        List<Group> originGroups = keePassFile.getGroups();
        List<Group> clonedGroups = clonedKeePassFile.getGroups();

        Assert.assertThat(originGroups, is(equalTo(clonedGroups)));

        List<Entry> originEntries = keePassFile.getEntries();
        List<Entry> clonedEntries = clonedKeePassFile.getEntries();

        Assert.assertThat(originEntries, is(equalTo(clonedEntries)));
    }

    private KeePassFile createFlatGroupStructure() {
        Group root = new GroupBuilder().addGroup(new GroupBuilder("A").build()).addGroup(new GroupBuilder("B").build()).addGroup(new GroupBuilder("C").build())
                .build();

        KeePassFile keePassFile = new KeePassFileBuilder("writeTreeDB").addTopGroups(root).build();

        return keePassFile;
    }
}
