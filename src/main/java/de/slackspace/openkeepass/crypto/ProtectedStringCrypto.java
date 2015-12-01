package de.slackspace.openkeepass.crypto;

public interface ProtectedStringCrypto {

	public String decrypt(String protectedString);
	
	public String encrypt(String plainString);
}
