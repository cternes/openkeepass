package de.slackspace.openkeepass.crypto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.slackspace.openkeepass.util.ByteUtils;

public class Salsa20Test {

    @Test(expected = IllegalArgumentException.class)
    public void whenStreamKeyIsNullShouldThrowArgumentException() {
        Salsa20.createInstance(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenProtectedStringIsNullShouldThrowArgumentException() {
        Salsa20.createInstance(new byte[10]).decrypt(null);
    }

    @Test
    public void whenInputIsStringShouldDecryptToPassword() {
        byte[] bytes = ByteUtils.hexStringToByteArray("ec77a2169769734c5d26e5341401f8d7b11052058f8455d314879075d0b7e257");
        String decrypted = Salsa20.createInstance(bytes).decrypt("U39tKvVEn9E=");
        assertThat(decrypted, is("Password"));
    }

    @Test
    public void shouldEncryptToHexString() {
        byte[] bytes = ByteUtils.hexStringToByteArray("ec77a2169769734c5d26e5341401f8d7b11052058f8455d314879075d0b7e257");
        String encrypted = Salsa20.createInstance(bytes).encrypt("Password");
        assertThat(encrypted, is("U39tKvVEn9E="));
    }
}
