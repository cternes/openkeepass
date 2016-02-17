package de.slackspace.openkeepass.domain;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;


public class CustomIconTest {

	@Test
	public void shouldEqual() {
		UUID uuid = UUID.randomUUID();
		CustomIcon iconOne = new CustomIconBuilder().uuid(uuid).data(new byte[4]).build();
		CustomIcon iconTwo = new CustomIconBuilder().uuid(uuid).data(new byte[4]).build();

		Assert.assertTrue(iconOne.equals(iconTwo));
		Assert.assertEquals(iconOne.hashCode(), iconTwo.hashCode());
	}

	@Test
	public void shouldNotEqual() {
		CustomIcon iconOne = new CustomIconBuilder().uuid(UUID.randomUUID()).data(new byte[4]).build();
		CustomIcon iconTwo = new CustomIconBuilder().uuid(UUID.randomUUID()).data(new byte[4]).build();

		Assert.assertFalse(iconOne.equals(iconTwo));
		Assert.assertNotEquals(iconOne.hashCode(), iconTwo.hashCode());
	}
}
