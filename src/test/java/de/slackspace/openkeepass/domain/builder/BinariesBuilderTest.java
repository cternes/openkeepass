package de.slackspace.openkeepass.domain.builder;

import java.util.ArrayList;
import java.util.List;

import de.slackspace.openkeepass.domain.Binaries;
import de.slackspace.openkeepass.domain.BinariesBuilder;
import de.slackspace.openkeepass.domain.Binary;
import de.slackspace.openkeepass.domain.BinaryBuilder;
import org.junit.Assert;
import org.junit.Test;

public class BinariesBuilderTest {

    @Test
    public void shouldBuildBinaries() {
        Binary binaryOne = createBinary(3, true);
        Binary binaryTwo = createBinary(4, false);

        Binaries binaries = new BinariesBuilder().addBinary(binaryOne).addBinary(binaryTwo).build();

        Assert.assertEquals(2, binaries.getBinaries().size());
    }

    @Test
    public void shouldFindBinaryById() {
        int idOne = 3;
        int idTwo = 4;
        boolean isCompressedOne = true;
        boolean isCompressedTwo = false;
        Binary binaryOne = createBinary(idOne, isCompressedOne);
        Binary binaryTwo = createBinary(idTwo, isCompressedTwo);

        Binaries binaries = new BinariesBuilder().addBinary(binaryOne).addBinary(binaryTwo).build();

        Assert.assertEquals(idOne, binaries.getBinaryById(idOne).getId());
        Assert.assertEquals(isCompressedOne, binaries.getBinaryById(idOne).isCompressed());
        Assert.assertEquals(idTwo, binaries.getBinaryById(idTwo).getId());
        Assert.assertEquals(isCompressedTwo, binaries.getBinaryById(idTwo).isCompressed());
    }

    @Test
    public void shouldBuildBinariesFromExisting() {
        List<Binary> binaryList = new ArrayList<Binary>();
        binaryList.add(createBinary(9, true));

        Binaries binaries = new BinariesBuilder().binaries(binaryList).build();
        Assert.assertEquals(1, binaries.getBinaries().size());

        Binaries binariesClone = new BinariesBuilder(binaries).build();
        Assert.assertEquals(1, binariesClone.getBinaries().size());
    }

    private Binary createBinary(int id, boolean isCompressed) {
        return new BinaryBuilder().id(id).isCompressed(isCompressed).build();
    }
}
