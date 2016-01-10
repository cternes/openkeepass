package de.slackspace.openkeepass.domain.builder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.domain.Meta;
import de.slackspace.openkeepass.domain.MetaBuilder;

public class MetaBuilderTest {

	@Test
	public void shouldBuildEntryWithDescriptionAndName() {
		Meta meta = new MetaBuilder("test")
			.databaseDescription("just a test db")
			.build();
		
		Assert.assertEquals("test", meta.getDatabaseName());
		Assert.assertEquals("just a test db", meta.getDatabaseDescription());
	}

	@Test
	public void shouldBuildEntryWithHistoryMaxItemsAndMaxSize() {
		Meta meta = new MetaBuilder("test")
				.historyMaxItems(10)
				.historyMaxSize(5)
				.build();
		
		Assert.assertEquals(10, meta.getHistoryMaxItems());
		Assert.assertEquals(5, meta.getHistoryMaxSize());
	}
	
	@Test
	public void shouldBuildEntryWithMaintenanceHistoryDays() {
		Meta meta = new MetaBuilder("test")
				.maintenanceHistoryDays(7)
				.build();
		
		Assert.assertEquals(7, meta.getMaintenanceHistoryDays());
	}
	
	@Test
	public void shouldBuildEntryWithChangeDates() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		Calendar descriptionChangeDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		descriptionChangeDate.set(2015, 1, 5, 0, 0, 0);
		
		Calendar nameChangeDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		nameChangeDate.set(2015, 3, 3, 0, 0, 0);
		
		Calendar recycleBinChangeDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		recycleBinChangeDate.set(2015, 6, 6, 0, 0, 0);
		
		Meta meta = new MetaBuilder("test")
				.databaseDescriptionChanged(descriptionChangeDate)
				.databaseNameChanged(nameChangeDate)
				.recycleBinChanged(recycleBinChangeDate)
				.build();
		
		Assert.assertEquals("2015-02-05 00:00:00", dateFormatter.format(meta.getDatabaseDescriptionChanged().getTime()));
		Assert.assertEquals("2015-04-03 00:00:00", dateFormatter.format(meta.getDatabaseNameChanged().getTime()));
		Assert.assertEquals("2015-07-06 00:00:00", dateFormatter.format(meta.getRecycleBinChanged().getTime()));
	}
	
	@Test
	public void shouldBuildEntryWithRecyclebinValues() {
		Meta meta = new MetaBuilder("test")
				.generator("openkeepass")
				.recycleBinEnabled(true)
				.recycleBinUuid(UUID.fromString("55702a7e-b4d8-41a1-9869-7aaa3f144ee2"))
				.build();
		
		Assert.assertEquals("openkeepass", meta.getGenerator());
		Assert.assertTrue(meta.getRecycleBinEnabled());
		Assert.assertEquals("55702a7e-b4d8-41a1-9869-7aaa3f144ee2", meta.getRecycleBinUuid().toString());
	}
	
	@Test
	public void shouldBuildEntryFromExistingEntry() {
		Meta meta = new MetaBuilder("test")
				.databaseDescription("abc")
				.databaseDescriptionChanged(Calendar.getInstance())
				.databaseNameChanged(Calendar.getInstance())
				.generator("openkeepass")
				.historyMaxItems(10)
				.historyMaxSize(15)
				.maintenanceHistoryDays(20)
				.recycleBinChanged(Calendar.getInstance())
				.recycleBinEnabled(true)
				.recycleBinUuid(UUID.randomUUID())
				.build();
		
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
		Meta meta = new MetaBuilder("test")
				.databaseDescription("abc")
				.databaseDescriptionChanged(Calendar.getInstance())
				.databaseNameChanged(Calendar.getInstance())
				.generator("openkeepass")
				.historyMaxItems(10)
				.historyMaxSize(15)
				.maintenanceHistoryDays(20)
				.recycleBinChanged(Calendar.getInstance())
				.recycleBinEnabled(true)
				.recycleBinUuid(UUID.randomUUID())
				.build();
		
		Meta metaCopyEqual = new MetaBuilder(meta).build();
		Assert.assertEquals(meta, metaCopyEqual);
		
		Meta metaCopyNotEqual = new MetaBuilder(meta).databaseName("something different").build();
		Assert.assertNotEquals(meta, metaCopyNotEqual);
	}
}
