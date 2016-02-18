package de.slackspace.openkeepass.domain;

import org.junit.Assert;
import org.junit.Test;

public class MetaTest {

	@Test
	public void shouldEqual() {
		Meta metaOne = new MetaBuilder("A").databaseDescription("empty").generator("openkeepass").historyMaxItems(10).historyMaxSize(7)
				.maintenanceHistoryDays(5).build();
		Meta metaTwo = new MetaBuilder("A").databaseDescription("empty").generator("openkeepass").historyMaxItems(10).historyMaxSize(7)
				.maintenanceHistoryDays(5).build();

		Assert.assertTrue(metaOne.equals(metaTwo));
		Assert.assertEquals(metaOne.hashCode(), metaTwo.hashCode());
	}

	@Test
	public void shouldNotEqual() {
		Meta metaOne = new MetaBuilder("A").databaseDescription("empty").generator("openkeepass").historyMaxItems(10).historyMaxSize(7)
				.maintenanceHistoryDays(5).build();
		Meta metaTwo = new MetaBuilder("B").databaseDescription("empty").generator("openkeepass").historyMaxItems(10).historyMaxSize(7)
				.maintenanceHistoryDays(5).build();

		Assert.assertFalse(metaOne.equals(metaTwo));
		Assert.assertNotEquals(metaOne.hashCode(), metaTwo.hashCode());
	}
}
