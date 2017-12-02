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

import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException;

public class Aes {

    private static final String MSG_KEY_MUST_NOT_BE_NULL = "Key must not be null";
    private static final String MSG_IV_MUST_NOT_BE_NULL = "IV must not be null";
    private static final String MSG_DATA_MUST_NOT_BE_NULL = "Data must not be null";
    private static final String KEY_TRANSFORMATION = "AES/ECB/NoPadding";
    private static final String DATA_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";

    private Aes() {
    }

    public static byte[] decrypt(byte[] key, byte[] ivRaw, byte[] data) {
        if (key == null) {
            throw new IllegalArgumentException(MSG_KEY_MUST_NOT_BE_NULL);
        }
        if (ivRaw == null) {
            throw new IllegalArgumentException(MSG_IV_MUST_NOT_BE_NULL);
        }
        if (data == null) {
            throw new IllegalArgumentException(MSG_DATA_MUST_NOT_BE_NULL);
        }

        return transformData(key, ivRaw, data, Cipher.DECRYPT_MODE);
    }

    public static byte[] encrypt(byte[] key, byte[] ivRaw, byte[] data) {
        if (key == null) {
            throw new IllegalArgumentException(MSG_KEY_MUST_NOT_BE_NULL);
        }
        if (ivRaw == null) {
            throw new IllegalArgumentException(MSG_IV_MUST_NOT_BE_NULL);
        }
        if (data == null) {
            throw new IllegalArgumentException(MSG_DATA_MUST_NOT_BE_NULL);
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
            throw new UnsupportedOperationException("The specified algorithm is unknown", e);
        } catch (NoSuchPaddingException e) {
            throw new UnsupportedOperationException("The specified padding is unknown", e);
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
        if (key == null) {
            throw new IllegalArgumentException(MSG_KEY_MUST_NOT_BE_NULL);
        }
        if (data == null) {
            throw new IllegalArgumentException(MSG_DATA_MUST_NOT_BE_NULL);
        }
        if (rounds < 1) {
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
            throw new UnsupportedOperationException("The specified algorithm is unknown", e);
        } catch (NoSuchPaddingException e) {
            throw new UnsupportedOperationException("The specified padding is unknown", e);
        } catch (InvalidKeyException e) {
            throw new KeePassDatabaseUnreadableException(
                    "The key has the wrong size. Have you installed Java Cryptography Extension (JCE)? Is the master key correct?", e);
        } catch (ShortBufferException e) {
            throw new AssertionError(e);
        }
    }

    private static KeePassDatabaseUnreadableException createCryptoException(Throwable e) {
        return new KeePassDatabaseUnreadableException("Could not decrypt keepass file. Master key wrong?", e);
    }

}
