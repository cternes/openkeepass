package de.slackspace.openkeepass.domain.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.EntryBuilder;

public class EntryFilterTest {

    @Test
    public void whenInputFilterIsMatchExactlyItShouldReturnMatchingEntries() {
        List<Entry> entries = createEntries();

        List<Entry> results = ListFilter.filter(entries, new Filter<Entry>() {
            @Override
            public boolean matches(Entry item) {
                if (item.getTitle().equalsIgnoreCase("test")) {
                    return true;
                }

                return false;
            };
        });

        assertThat(results.size(), is(1));
        assertThat(results.get(0).getTitle(), is("test"));
    }

    private List<Entry> createEntries() {
        ArrayList<Entry> list = new ArrayList<Entry>();
        list.add(createEntry("test", "testPassword"));
        list.add(createEntry("My simple test case", "My simple test password"));
        list.add(createEntry("AAA BBB ccc", "aa bb CC"));

        return list;
    }

    private Entry createEntry(String title, String password) {
        Entry entry = new EntryBuilder(title).password(password).build();

        return entry;
    }

}
