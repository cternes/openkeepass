package de.slackspace.openkeepass.domain.builder;

import java.util.Calendar;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.domain.Meta;
import de.slackspace.openkeepass.domain.MetaBuilder;
import de.slackspace.openkeepass.util.CalendarHandler;

public class MetaBuilderTest {

    @Test
    public void shouldBuildEntryWithDescriptionAndName() {
        Meta meta = new MetaBuilder("test").databaseDescription("just a test db").build();

        Assert.assertEquals("test", meta.getDatabaseName());
        Assert.assertEquals("just a test db", meta.getDatabaseDescription());
    }

    @Test
    public void shouldBuildEntryWithHistoryMaxItemsAndMaxSize() {
        Meta meta = new MetaBuilder("test").historyMaxItems(10).historyMaxSize(5).build();

        Assert.assertEquals(10, meta.getHistoryMaxItems());
        Assert.assertEquals(5, meta.getHistoryMaxSize());
    }

    @Test
    public void shouldBuildEntryWithMaintenanceHistoryDays() {
        Meta meta = new MetaBuilder("test").maintenanceHistoryDays(7).build();

        Assert.assertEquals(7, meta.getMaintenanceHistoryDays());
    }

    @Test
    public void shouldBuildEntryWithChangeDates() {
        Meta meta = new MetaBuilder("test").databaseDescriptionChanged(CalendarHandler.createCalendar(2015, 2, 5))
                .databaseNameChanged(CalendarHandler.createCalendar(2015, 4, 3)).recycleBinChanged(CalendarHandler.createCalendar(2015, 7, 6)).build();

        Assert.assertEquals("2015-02-05 00:00:00", CalendarHandler.formatCalendar(meta.getDatabaseDescriptionChanged()));
        Assert.assertEquals("2015-04-03 00:00:00", CalendarHandler.formatCalendar(meta.getDatabaseNameChanged()));
        Assert.assertEquals("2015-07-06 00:00:00", CalendarHandler.formatCalendar(meta.getRecycleBinChanged()));
    }

    @Test
    public void shouldBuildEntryWithRecyclebinValues() {
        Meta meta = new MetaBuilder("test").generator("openkeepass").recycleBinEnabled(true)
                .recycleBinUuid(UUID.fromString("55702a7e-b4d8-41a1-9869-7aaa3f144ee2")).build();

        Assert.assertEquals("openkeepass", meta.getGenerator());
        Assert.assertTrue(meta.getRecycleBinEnabled());
        Assert.assertEquals("55702a7e-b4d8-41a1-9869-7aaa3f144ee2", meta.getRecycleBinUuid().toString());
    }

    @Test
    public void shouldBuildEntryFromExistingEntry() {
        Meta meta = new MetaBuilder("test").databaseDescription("abc").databaseDescriptionChanged(Calendar.getInstance())
                .databaseNameChanged(Calendar.getInstance()).generator("openkeepass").historyMaxItems(10).historyMaxSize(15).maintenanceHistoryDays(20)
                .recycleBinChanged(Calendar.getInstance()).recycleBinEnabled(true).recycleBinUuid(UUID.randomUUID()).build();

        Meta metaCopy = new MetaBuilder(meta).build();

        Assert.assertEquals(meta.getDatabaseDescription(), metaCopy.getDatabaseDescription());
        Assert.assertEquals(meta.getDatabaseDescriptionChanged(), metaCopy.getDatabaseDescriptionChanged());
        Assert.assertEquals(meta.getDatabaseNameChanged(), metaCopy.getDatabaseNameChanged());
        Assert.assertEquals(meta.getGenerator(), metaCopy.getGenerator());
        Assert.assertEquals(meta.getHistoryMaxItems(), metaCopy.getHistoryMaxItems());
        Assert.assertEquals(meta.getHistoryMaxSize(), metaCopy.getHistoryMaxSize());
        Assert.assertEquals(meta.getMaintenanceHistoryDays(), metaCopy.getMaintenanceHistoryDays());
        Assert.assertEquals(meta.getRecycleBinChanged(), metaCopy.getRecycleBinChanged());
        Assert.assertEquals(meta.getRecycleBinEnabled(), metaCopy.getRecycleBinEnabled());
        Assert.assertEquals(meta.getRecycleBinUuid(), metaCopy.getRecycleBinUuid());
    }

    @Test
    public void shouldCheckEqualityOfObjects() {
        Meta meta = new MetaBuilder("test").databaseDescription("abc").databaseDescriptionChanged(Calendar.getInstance())
                .databaseNameChanged(Calendar.getInstance()).generator("openkeepass").historyMaxItems(10).historyMaxSize(15).maintenanceHistoryDays(20)
                .recycleBinChanged(Calendar.getInstance()).recycleBinEnabled(true).recycleBinUuid(UUID.randomUUID()).build();

        Meta metaCopyEqual = new MetaBuilder(meta).build();
        Assert.assertEquals(meta, metaCopyEqual);

        Meta metaCopyNotEqual = new MetaBuilder(meta).databaseName("something different").build();
        Assert.assertNotEquals(meta, metaCopyNotEqual);
    }
}
