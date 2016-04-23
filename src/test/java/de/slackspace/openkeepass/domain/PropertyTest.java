package de.slackspace.openkeepass.domain;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class PropertyTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Property.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
