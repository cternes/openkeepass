package de.slackspace.openkeepass.crypto;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RandomGeneratorTest {

    @Test
    public void shouldGeneratorOneRandomByte() {
        RandomGenerator randomGenerator = new RandomGenerator();
        byte[] randomBytes = randomGenerator.getRandomBytes(1);
        assertThat(randomBytes, is(notNullValue()));
        assertThat(randomBytes.length, is(1));
    }

    @Test
    public void shouldGeneratorFourRandomBytes() {
        RandomGenerator randomGenerator = new RandomGenerator();
        byte[] randomBytes = randomGenerator.getRandomBytes(4);

        assertThat(randomBytes, is(notNullValue()));
        assertThat(randomBytes.length, is(4));
    }
}
