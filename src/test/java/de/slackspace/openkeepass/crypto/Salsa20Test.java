package de.slackspace.openkeepass.crypto;

import junit.framework.Assert;

import org.junit.Test;

import de.slackspace.openkeepass.util.ByteUtils;

public class Salsa20Test {

	@Test
	public void whenInputIsStringShouldDecryptToPassword() {
		byte[] bytes = ByteUtils.hexStringToByteArray("ec77a2169769734c5d26e5341401f8d7b11052058f8455d314879075d0b7e257");
		Assert.assertEquals("Password", Salsa20.createInstance(bytes).decrypt("U39tKvVEn9E="));
	}
}
