package de.slackspace.openkeepass.crypto;

import java.io.UnsupportedEncodingException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public class Salsa20 implements ProtectedStringCrypto {

	private static final String MSG_UNKNOWN_UTF8_ENCODING = "The encoding UTF-8 is not supported";
	private static final String SALSA20_ALGORITHM = "Salsa20";
	private static final String ENCODING = "UTF-8";
	private static final String SALSA20IV = "E830094B97205D2A";

	private Cipher salsa20Engine;

	private Salsa20() {
		Security.addProvider(new BouncyCastleProvider());
	}

	private void initialize(byte[] protectedStreamKey) {
		byte[] salsaKey = Sha256.hash(protectedStreamKey);

		try {
			salsa20Engine = Cipher.getInstance(SALSA20_ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
			salsa20Engine.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(salsaKey, SALSA20_ALGORITHM),
					new IvParameterSpec(Hex.decode(SALSA20IV)));
		} catch (Exception e) {
			throw new UnsupportedOperationException("Could not find provider '" + SALSA20_ALGORITHM + "'", e);
		}
	}

	public static Salsa20 createInstance(byte[] protectedStreamKey) {
		if (protectedStreamKey == null) {
			throw new IllegalArgumentException("ProtectedStreamKey must not be null");
		}

		Salsa20 salsa20 = new Salsa20();
		salsa20.initialize(protectedStreamKey);

		return salsa20;
	}

	@Override
	public String decrypt(String protectedString) {
		if (protectedString == null) {
			throw new IllegalArgumentException("ProtectedString must not be null");
		}

		byte[] protectedBuffer = Base64.decode(protectedString.getBytes());
		byte[] plainText = new byte[protectedBuffer.length];

		try {
			salsa20Engine.update(protectedBuffer, 0, protectedBuffer.length, plainText, 0);
			return new String(plainText, ENCODING);
		} catch (ShortBufferException e) {
			throw new AssertionError(e);
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(MSG_UNKNOWN_UTF8_ENCODING, e);
		}
	}

	@Override
	public String encrypt(String plainString) {
		if (plainString == null) {
			throw new IllegalArgumentException("PlainString must not be null");
		}

		try {
			byte[] plainStringBytes = plainString.getBytes(ENCODING);
			byte[] encodedText = new byte[plainStringBytes.length];

			salsa20Engine.update(plainStringBytes, 0, plainStringBytes.length, encodedText, 0);

			byte[] protectedBuffer = Base64.encode(encodedText);

			return new String(protectedBuffer, ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(MSG_UNKNOWN_UTF8_ENCODING, e);
		} catch (ShortBufferException e) {
			throw new AssertionError(e);
		}
	}

}
