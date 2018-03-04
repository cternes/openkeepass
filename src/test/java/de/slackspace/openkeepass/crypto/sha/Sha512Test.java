package de.slackspace.openkeepass.crypto.sha;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.slackspace.openkeepass.util.ByteUtils;

public class Sha512Test {

    @Test
    public void whenInputIsStringOutputShouldBeHashed() {
        byte[] hash = Sha512.getInstance().hash("test");

        assertThat(hash,
                is(ByteUtils.hexStringToByteArray(
                        "ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenInputStringIsNullShouldThrowArgumentException() {
        String input = null;
        Sha512.getInstance().hash(input);
    }

    @Test
    public void whenInputIsByteOutputShouldBeHashed() {
        byte[] hash = Sha512.getInstance().hash(ByteUtils.hexStringToByteArray("afc47cff"));
        assertThat(hash,
                is(ByteUtils.hexStringToByteArray(
                        "3f45d479d666d9ba332d02dc5efcaa0117f28ad7e3310e334fe07bc79b48ec95a3e91520b341ffc1bb157167b5af5eaadffa8b0eabb0a05ed4354f9925ee6f02")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenInputBytesIsNullShouldThrowArgumentException() {
        byte[] input = null;
        Sha512.getInstance().hash(input);
    }
}
