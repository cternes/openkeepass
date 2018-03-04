package de.slackspace.openkeepass.crypto.sha;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.slackspace.openkeepass.util.ByteUtils;

public class Sha256Test {

    @Test
    public void whenInputIsStringOutputShouldBeHashed() {
        byte[] hash = Sha256.getInstance().hash("test");
        assertThat(hash,
                is(ByteUtils.hexStringToByteArray("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenInputStringIsNullShouldThrowArgumentException() {
        String input = null;
        Sha256.getInstance().hash(input);
    }

    @Test
    public void whenInputIsByteOutputShouldBeHashed() {
        byte[] hash = Sha256.getInstance().hash(ByteUtils.hexStringToByteArray("afc47cff"));
        assertThat(hash,
                is(ByteUtils.hexStringToByteArray("e3f4a8d4eeedd1cada0c4cf2e2f457343a18544160292aa83e3b8d59f23bc375")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenInputBytesIsNullShouldThrowArgumentException() {
        byte[] input = null;
        Sha256.getInstance().hash(input);
    }
}
