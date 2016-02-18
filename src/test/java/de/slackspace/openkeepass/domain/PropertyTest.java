package de.slackspace.openkeepass.domain;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class PropertyTest {

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Property.class).verify();
	}
}
