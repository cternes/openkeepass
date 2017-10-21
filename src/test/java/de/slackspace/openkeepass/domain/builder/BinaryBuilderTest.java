package de.slackspace.openkeepass.domain.builder;

import de.slackspace.openkeepass.domain.Binary;
import de.slackspace.openkeepass.domain.BinaryBuilder;
import org.junit.Assert;
import org.junit.Test;

public class BinaryBuilderTest {

    @Test
    public void shouldBuildBinary() {
        int id = 5;
        boolean isCompressed = true;
        byte[] data = new byte[4];

        Binary binary = new BinaryBuilder().id(id).isCompressed(isCompressed).data(data).build();

        Assert.assertEquals(id, binary.getId());
        Assert.assertEquals(isCompressed, binary.isCompressed());
        Assert.assertEquals(data, binary.getData());
    }

    @Test
    public void shouldBuildBinaryFromExistingBinary() {
        int id = 5;
        boolean isCompressed = true;
        byte[] data = new byte[4];

        Binary binary = new BinaryBuilder().id(id).isCompressed(isCompressed).data(data).build();
        Binary binaryClone = new BinaryBuilder(binary).build();

        Assert.assertEquals(id, binaryClone.getId());
        Assert.assertEquals(isCompressed, binaryClone.isCompressed());
        Assert.assertEquals(data, binaryClone.getData());
    }
}
