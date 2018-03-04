package de.slackspace.openkeepass.crypto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.slackspace.openkeepass.util.ByteUtils;

public class HmacSha256Test {

    @Test
    public void whenInputIsGivenOutputShouldBeHashed() {
        // arrange
        byte[] key = "myKey".getBytes();
        byte[] input = "myInput".getBytes();

        // act
        byte[] hash = HmacSha256.getInstance(key).doFinal(input);

        // assert
        assertThat(hash,
                is(ByteUtils.hexStringToByteArray("cb6038b8032a74d9a6c4b03640297c747bcf646ba29b7263758d0ca8a9a444a8")));
    }

    @Test
    public void whenInputIsUpdatedOutputShouldBeHashed() {
        // arrange
        byte[] key = "myKey".getBytes();
        byte[] input = "myInput".getBytes();
        byte[] update = ByteUtils.hexStringToByteArray("A4FF6E1A");

        // act
        byte[] mac = HmacSha256.getInstance(key)
                .update(update)
                .doFinal(input);

        // assert
        assertThat(mac,
                is(ByteUtils.hexStringToByteArray("3f780a362db2e13ad002a5c595f07da9ee6574bfcb6381a4707eb6b4bef450e5")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenInputIsNullShouldThrowArgumentException() {
        byte[] input = null;
        HmacSha256.getInstance(input);
    }
}
