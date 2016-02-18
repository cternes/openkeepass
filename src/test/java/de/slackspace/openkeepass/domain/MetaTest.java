package de.slackspace.openkeepass.domain;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class MetaTest {

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Meta.class).verify();
	}

}
