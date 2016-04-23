package de.slackspace.openkeepass.domain;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class TimesTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Times.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
