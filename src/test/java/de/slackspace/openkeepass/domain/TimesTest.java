package de.slackspace.openkeepass.domain;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class TimesTest {

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Times.class).verify();
	}
}
