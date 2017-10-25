package de.slackspace.openkeepass.domain.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.slackspace.openkeepass.domain.Binaries;
import de.slackspace.openkeepass.domain.BinariesBuilder;
import de.slackspace.openkeepass.domain.Binary;
import de.slackspace.openkeepass.domain.BinaryBuilder;

public class BinariesBuilderTest {

    @Test
    public void shouldBuildBinaries() {
        Binary binaryOne = createBinary(3, true);
        Binary binaryTwo = createBinary(4, false);

        Binaries binaries = new BinariesBuilder().addBinary(binaryOne).addBinary(binaryTwo).build();

        assertThat(binaries.getBinaries().size(), is(2));
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

        assertThat(binaries.getBinaryById(idOne).getId(), is(idOne));
        assertThat(binaries.getBinaryById(idOne).isCompressed(), is(isCompressedOne));

        assertThat(binaries.getBinaryById(idTwo).getId(), is(idTwo));
        assertThat(binaries.getBinaryById(idTwo).isCompressed(), is(isCompressedTwo));
    }

    @Test
    public void shouldBuildBinariesFromExisting() {
        List<Binary> binaryList = new ArrayList<Binary>();
        binaryList.add(createBinary(9, true));

        Binaries binaries = new BinariesBuilder().binaries(binaryList).build();
        assertThat(binaries.getBinaries().size(), is(1));

        Binaries binariesClone = new BinariesBuilder(binaries).build();
        assertThat(binariesClone.getBinaries().size(), is(1));
    }

    private Binary createBinary(int id, boolean isCompressed) {
        return new BinaryBuilder().id(id).isCompressed(isCompressed).build();
    }
}
