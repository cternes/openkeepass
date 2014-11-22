package de.slackspace.openkeepass.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256 {

	public static byte[] hash(String text) {
		try {
			return hash(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException("The enconding is not supported");
		}
	}
	
	public static byte[] hash(byte[] bytes) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(bytes);
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException("The algorithm is not supported");
		}
	}
}
