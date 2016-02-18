package de.slackspace.openkeepass.domain;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class EntryTest {

	@Test
	public void shouldEqual() {
		UUID uuid = UUID.randomUUID();
		Entry entryOne = new EntryBuilder(uuid).username("user").password("passwd").url("http://test.com").build();
		Entry entryTwo = new EntryBuilder(uuid).username("user").password("passwd").url("http://test.com").build();

		Assert.assertTrue(entryOne.equals(entryTwo));
		Assert.assertEquals(entryOne.hashCode(), entryTwo.hashCode());
	}

	@Test
	public void shouldNotEqual() {
		Entry entryOne = new EntryBuilder("A").username("user").password("passwd").url("http://test.com").build();
		Entry entryTwo = new EntryBuilder("B").username("user").password("passwd").url("http://test.com").build();

		Assert.assertFalse(entryOne.equals(entryTwo));
		Assert.assertNotEquals(entryOne.hashCode(), entryTwo.hashCode());
	}
}
