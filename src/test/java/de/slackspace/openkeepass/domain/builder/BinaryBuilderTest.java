package de.slackspace.openkeepass.domain.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.slackspace.openkeepass.domain.Binary;
import de.slackspace.openkeepass.domain.BinaryBuilder;

public class BinaryBuilderTest {

    @Test
    public void shouldBuildBinary() {
        int id = 5;
        boolean isCompressed = false;
        byte[] data = new byte[4];

        Binary binary = new BinaryBuilder().id(id).isCompressed(isCompressed).data(data).build();

        assertThat(binary.getId(), is(id));
        assertThat(binary.isCompressed(), is(isCompressed));
        assertThat(binary.getData(), is(data));
    }

    @Test
    public void shouldBuildBinaryFromExistingBinary() {
        int id = 5;
        boolean isCompressed = false;
        byte[] data = new byte[4];

        Binary binary = new BinaryBuilder().id(id).isCompressed(isCompressed).data(data).build();

        assertThat(binary.getId(), is(id));
        assertThat(binary.isCompressed(), is(isCompressed));
        assertThat(binary.getData(), is(data));
    }
}
