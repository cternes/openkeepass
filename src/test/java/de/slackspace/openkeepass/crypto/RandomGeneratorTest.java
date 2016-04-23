package de.slackspace.openkeepass.crypto;

import org.junit.Assert;
import org.junit.Test;

public class RandomGeneratorTest {

    @Test
    public void shouldGeneratorOneRandomByte() {
        RandomGenerator randomGenerator = new RandomGenerator();
        byte[] randomBytes = randomGenerator.getRandomBytes(1);
        Assert.assertNotNull(randomBytes);
        Assert.assertEquals(randomBytes.length, 1);
    }

    @Test
    public void shouldGeneratorFourRandomBytes() {
        RandomGenerator randomGenerator = new RandomGenerator();
        byte[] randomBytes = randomGenerator.getRandomBytes(4);
        Assert.assertNotNull(randomBytes);
        Assert.assertEquals(randomBytes.length, 4);
    }
}
