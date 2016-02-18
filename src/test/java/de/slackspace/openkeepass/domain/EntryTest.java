package de.slackspace.openkeepass.domain;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class EntryTest {

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Entry.class).verify();
	}
}
