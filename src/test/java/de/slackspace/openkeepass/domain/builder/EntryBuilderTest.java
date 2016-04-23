package de.slackspace.openkeepass.domain.builder;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.EntryBuilder;
import de.slackspace.openkeepass.domain.History;

public class EntryBuilderTest {

    @Test
    public void shouldBuildEntryWithUUIDAndTitle() {
        UUID uuid = UUID.randomUUID();
        Entry entry = new EntryBuilder(uuid).title("test").build();

        Assert.assertEquals(uuid, entry.getUuid());
        Assert.assertEquals("test", entry.getTitle());
    }

    @Test
    public void shouldBuildEntryWithTitleAndRandomUUID() {
        Entry entry = new EntryBuilder("test").build();

        Assert.assertNotNull(entry.getUuid());
        Assert.assertEquals("test", entry.getTitle());
    }

    @Test
    public void shouldBuildEntryWithTitleAndGivenUUID() {
        UUID uuid = UUID.randomUUID();
        Entry entry = new EntryBuilder("test").uuid(uuid).build();

        Assert.assertEquals(uuid, entry.getUuid());
        Assert.assertEquals("test", entry.getTitle());
    }

    @Test
    public void shouldBuildEntryFromEntryWithHistory() {
        UUID uuid = UUID.randomUUID();
        Entry entry = new EntryBuilder("historytest").uuid(uuid).build();

        EntryBuilder entryBuilder = new EntryBuilder(entry).username("test user name");

        Entry createdEntry = entryBuilder.buildWithHistory();
        Assert.assertEquals("should be 'test user name'", "test user name", createdEntry.getUsername());

        History history = createdEntry.getHistory();
        Assert.assertNotNull("history should not be null", history);
        Assert.assertEquals("history size should be 1", 1, history.getHistoricEntries().size());

        Entry historicEntry = history.getHistoricEntries().get(0);
        Assert.assertEquals("title should be historytest", "historytest", historicEntry.getTitle());
        Assert.assertNull("username of the history should be null", historicEntry.getUsername());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWithNoEntrySet() {
        new EntryBuilder("test").buildWithHistory();
    }

    @Test
    public void shouldCheckEqualityOfObjects() {
        Entry entry = new EntryBuilder().title("test").notes("a note").password("secret").username("user").url("myUrl").build();

        Entry entryCopyEqual = new EntryBuilder(entry).build();
        Assert.assertEquals(entry, entryCopyEqual);

        Entry entryCopyNotEqual = new EntryBuilder(entry).notes("another note").build();
        Assert.assertNotEquals(entry, entryCopyNotEqual);
    }
}
