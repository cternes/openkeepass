package de.slackspace.openkeepass.domain;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.slackspace.openkeepass.domain.zipper.GroupZipper;

public class GroupZipperTest {

	@Test
	public void shouldNavigateThroughTreeAndReplaceGroupAndEntryNode() {
		/* Should create the following structure:
		 * 
		 * Root
		 * |
		 * |-- First entry (E)
		 * |-- Banking (G)
		 * |
		 * |-- Internet (G)
		 *     |
		 *     |-- Shopping (G)
		 *     	   |-- Second entry (E)
		 *  
		 */
		Group root = new GroupBuilder()
				.addEntry(new EntryBuilder("First entry").build())
				.addGroup(new GroupBuilder("Banking").build())
				.addGroup(new GroupBuilder("Internet")
						.addGroup(new GroupBuilder("Shopping")
								.addEntry(new EntryBuilder("Second entry").build())
								.build())
						.build())
				.build();
		
		KeePassFile keePassFile = new KeePassFileBuilder("writeTreeDB")
				.addTopGroups(root)
				.build();		
		
		GroupZipper zipper = new GroupZipper(keePassFile).down().right().down();
		Group shoppingGroup = zipper.getNode();
		Entry shoppingEntry = shoppingGroup.getEntryByTitle("Second entry");
		Group modifiedGroup = new GroupBuilder(shoppingGroup).name("Fashion")
				.removeEntry(shoppingEntry)
				.addEntry(new EntryBuilder(shoppingEntry).title("Entry #2").build())
				.build();
		
		zipper = zipper.replace(modifiedGroup);
		
		KeePassFile keePassFileModified = zipper.close();
		Assert.assertNotNull(keePassFileModified.getGroupByName("Fashion"));
		Assert.assertNotNull(keePassFileModified.getEntryByTitle("Entry #2"));
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

	private KeePassFile createFlatGroupStructure() {
		Group root = new GroupBuilder()
				.addGroup(new GroupBuilder("A").build())
				.addGroup(new GroupBuilder("B").build())
				.addGroup(new GroupBuilder("C").build())
				.build();
		
		KeePassFile keePassFile = new KeePassFileBuilder("writeTreeDB")
				.addTopGroups(root)
				.build();
		
		return keePassFile;
	}
}
