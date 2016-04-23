package de.slackspace.openkeepass.crypto;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.util.ByteUtils;

public class Sha256Test {

    @Test
    public void whenInputIsStringOutputShouldBeHashed() {
        byte[] hash = Sha256.hash("test");
        Assert.assertTrue("Hash must be 9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08",
                Arrays.equals(ByteUtils.hexStringToByteArray("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"), hash));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenInputStringIsNullShouldThrowArgumentException() {
        String input = null;
        Sha256.hash(input);
    }

    @Test
    public void whenInputIsByteOutputShouldBeHashed() {
        byte[] hash = Sha256.hash(ByteUtils.hexStringToByteArray("afc47cff"));
        Assert.assertTrue("Hash must be e3f4a8d4eeedd1cada0c4cf2e2f457343a18544160292aa83e3b8d59f23bc375",
                Arrays.equals(ByteUtils.hexStringToByteArray("e3f4a8d4eeedd1cada0c4cf2e2f457343a18544160292aa83e3b8d59f23bc375"), hash));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenInputBytesIsNullShouldThrowArgumentException() {
        byte[] input = null;
        Sha256.hash(input);
    }
}
