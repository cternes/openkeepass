package de.slackspace.openkeepass.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadable;

public class Aes {

	private static final String KEY_TRANSFORMATION = "AES/ECB/NoPadding";
	private static final String DATA_TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final String KEY_ALGORITHM = "AES";
	
	public static byte[] decrypt(byte[] key, byte[] ivRaw, byte[] encryptedData) {
		if(key == null) {
			throw new IllegalArgumentException("Key must not be null");
		}
		if(ivRaw == null) {
			throw new IllegalArgumentException("IV must not be null");
		}
		if(encryptedData == null) {
			throw new IllegalArgumentException("EncryptedData must not be null");
		}
		
		return transformData(key, ivRaw, encryptedData, Cipher.DECRYPT_MODE);
	}
	
	public static byte[] encrypt(byte[] key, byte[] ivRaw, byte[] data) {
		if(key == null) {
			throw new IllegalArgumentException("Key must not be null");
		}
		if(ivRaw == null) {
			throw new IllegalArgumentException("IV must not be null");
		}
		if(data == null) {
			throw new IllegalArgumentException("Data must not be null");
		}
		
		return transformData(key, ivRaw, data, Cipher.ENCRYPT_MODE);
	}

	private static byte[] transformData(byte[] key, byte[] ivRaw, byte[] encryptedData, int operationMode) {
		try {
			Cipher cipher = Cipher.getInstance(DATA_TRANSFORMATION);
			Key aesKey = new SecretKeySpec(key, KEY_ALGORITHM);
			IvParameterSpec iv = new IvParameterSpec(ivRaw);
			cipher.init(operationMode, aesKey, iv);
			return cipher.doFinal(encryptedData);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw createCryptoException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw createCryptoException(e);
		} catch (IllegalBlockSizeException e) {
			throw createCryptoException(e);
		} catch (BadPaddingException e) {
			throw createCryptoException(e);
		}
	}
	
	public static byte[] transformKey(byte[] key, byte[] data, long rounds) {
		if(key == null) {
			throw new IllegalArgumentException("Key must not be null");
		}
		if(data == null) {
			throw new IllegalArgumentException("Data must not be null");
		}
		if(rounds < 1) {
			throw new IllegalArgumentException("Rounds must be > 1");
		}
		
		try {
			Cipher c = Cipher.getInstance(KEY_TRANSFORMATION);
			Key aesKey = new SecretKeySpec(key, KEY_ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, aesKey);
			
			for (long i = 0; i < rounds; ++i) {
				c.update(data, 0, 16, data, 0);
				c.update(data, 16, 16, data, 16);
			}
			
			return data;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("The key has the wrong size. Have you installed Java Cryptography Extension (JCE)?", e);
		} catch (ShortBufferException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static KeePassDatabaseUnreadable createCryptoException(Throwable e) {
		return new KeePassDatabaseUnreadable("Could not decrypt keepass file. Master key wrong?", e);
	}

}
