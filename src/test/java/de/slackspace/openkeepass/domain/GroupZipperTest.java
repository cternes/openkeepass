package de.slackspace.openkeepass.domain;

import org.junit.Assert;
import org.junit.Test;

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
}
