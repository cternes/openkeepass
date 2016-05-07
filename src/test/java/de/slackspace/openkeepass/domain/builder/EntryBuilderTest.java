package de.slackspace.openkeepass.domain.builder;

import java.util.Calendar;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.EntryBuilder;
import de.slackspace.openkeepass.domain.History;
import de.slackspace.openkeepass.domain.Times;
import de.slackspace.openkeepass.domain.TimesBuilder;
import de.slackspace.openkeepass.util.CalendarHandler;

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

    @Test
    public void shouldBuildEntryWithTimes() {
        Calendar creationDate = CalendarHandler.createCalendar(2016, 2, 5);

        Times times = new TimesBuilder().expires(true).usageCount(3).creationTime(creationDate).build();
        Entry entry = new EntryBuilder("timesTest").times(times).build();

        Assert.assertTrue(entry.getTimes().expires());
        Assert.assertEquals(3, entry.getTimes().getUsageCount());
        Assert.assertEquals(creationDate, entry.getTimes().getCreationTime());
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

    @Test
    public void shouldClearHistoryOfOriginalEntryOnBuildWithHistory() {
        Entry entry = new EntryBuilder("v1").build();
        Entry entryTwo = new EntryBuilder(entry).title("v2").buildWithHistory();
        Entry entryThree = new EntryBuilder(entryTwo).title("v3").buildWithHistory();

        for (Entry historicEntry : entryThree.getHistory().getHistoricEntries()) {
            Assert.assertEquals(0, historicEntry.getHistory().getHistoricEntries().size());
        }
    }
}
