package de.slackspace.openkeepass.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import de.slackspace.openkeepass.domain.ByteGenerator;

public class RandomGenerator implements ByteGenerator {

    private SecureRandom random;

    public RandomGenerator() {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Algorithm 'SHA1PRNG' is unknown", e);
        }
    }

    @Override
    public byte[] getRandomBytes(int numBytes) {
        byte[] randomBytes = new byte[numBytes];
        random.nextBytes(randomBytes);

        return randomBytes;
    }
}
