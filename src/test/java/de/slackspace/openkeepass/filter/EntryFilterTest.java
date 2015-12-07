package de.slackspace.openkeepass.filter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.Property;

public class EntryFilterTest {

	@Test
	public void whenInputFilterIsMatchExactlyItShouldReturnMatchingEntries() {
		List<Entry> entries = createEntries();
		
		List<Entry> results = ListFilter.filter(entries, new Filter<Entry>() {
			public boolean matches(Entry item) {
				if(item.getTitle().equalsIgnoreCase("test")) {
					return true;
				}
				
				return false;
			};
		});
		
		Assert.assertEquals(1, results.size());
		Assert.assertEquals("test", results.get(0).getTitle());
	}
	
	private List<Entry> createEntries() {
		ArrayList<Entry> list = new ArrayList<Entry>();
		list.add(createEntry("1", "test", "testPassword"));
		list.add(createEntry("2", "My simple test case", "My simple test password"));
		list.add(createEntry("3", "AAA BBB ccc", "aa bb CC"));

		return list;
	}

	private Entry createEntry(String uuid, String title, String password) {
		Entry entry = new Entry(uuid);
		
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property("Title", title, false));
		properties.add(new Property("Password", password, false));
		entry.setProperties(properties);
		
		return entry;
	}
	
}
