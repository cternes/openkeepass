package de.slackspace.openkeepass.domain;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;


public class CustomIconTest {

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(CustomIcon.class).verify();
	}
}
