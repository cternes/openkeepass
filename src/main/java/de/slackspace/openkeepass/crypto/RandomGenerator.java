package de.slackspace.openkeepass.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class RandomGenerator {

	private SecureRandom random;

	public RandomGenerator() {
		try {
			random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException("Algorithm 'SHA1PRNG' is unknown", e);
		} catch (NoSuchProviderException e) {
			throw new UnsupportedOperationException("Provider 'SUN' is unknown", e);
		}
	}

	public byte[] getRandomBytes(int numBytes) {
		byte[] randomBytes = new byte[numBytes];
		random.nextBytes(randomBytes);

		return randomBytes;
	}
}
