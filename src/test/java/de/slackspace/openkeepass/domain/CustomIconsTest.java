package de.slackspace.openkeepass.domain;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CustomIconsTest {

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(CustomIcons.class).verify();
	}
}