package de.slackspace.openkeepass.crypto;

public interface ProtectedStringCrypto {

    /**
     * Decrypts a given encrypted string and returns it.
     * 
     * @param protectedString
     *            the encrypted string
     * @return the input string unencrypted
     */
    public String decrypt(String protectedString);

    /**
     * Encrypts a given string and returns it.
     * 
     * @param plainString
     *            the unencrypted string
     * @return the input string encrypted
     */
    public String encrypt(String plainString);
}
