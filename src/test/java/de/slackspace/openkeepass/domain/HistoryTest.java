package de.slackspace.openkeepass.domain;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class HistoryTest {

    @Test
    public void equalsContract() {
        Entry red = new EntryBuilder("Red").build();
        Entry black = new EntryBuilder("Black").build();
        
        EqualsVerifier.forClass(History.class)
            .withPrefabValues(Entry.class, red, black)
            .suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
