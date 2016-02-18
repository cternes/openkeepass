package de.slackspace.openkeepass.domain;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class GroupTest {

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Group.class).verify();
	}
}
