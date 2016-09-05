package de.slackspace.openkeepass.crypto;

import java.lang.reflect.Field;
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
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class Aes {

    private static final String MSG_KEY_MUST_NOT_BE_NULL = "Key must not be null";
    private static final String MSG_IV_MUST_NOT_BE_NULL = "IV must not be null";
    private static final String MSG_DATA_MUST_NOT_BE_NULL = "Data must not be null";
    private static final String KEY_TRANSFORMATION = "AES/ECB/NoPadding";
    private static final String DATA_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";

    private Aes() {
    }

    static {
        tryAvoidJCE();
    }

    private static void tryAvoidJCE() {
        try {
            setJceSecurityUnrestricted();
        }
        catch (Exception e) {
            try {
                setJceSecurityUnrestricted(getUnsafe());
            }
            catch (Exception e1) {
                // ignore, the user will have to install JCE manually
            }
        }
    }
    
    private static void setJceSecurityUnrestricted() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException {
        Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
        field.setAccessible(true);
        field.setBoolean(null, false);
    }
    
    @SuppressWarnings("restriction")
    private static void setJceSecurityUnrestricted(Unsafe unsafe) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
        unsafe.putBoolean(Class.forName("javax.crypto.JceSecurity"), unsafe.staticFieldOffset(field), false);
    }
    
    @SuppressWarnings("restriction")
    private static Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
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
