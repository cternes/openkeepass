package de.slackspace.openkeepass.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacSha256 {

    private static final String ALGORITHM = "HmacSHA256";
    private Mac mac;

    private HmacSha256(byte[] key) {
        init(key);
    }

    public static HmacSha256 getInstance(byte[] key) {
        return new HmacSha256(key);
    }

    public void init(byte[] key) {
        try {
            mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
            mac.init(secretKeySpec);
        }
        catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(String.format("The algorithm '%s' is not supported", ALGORITHM), e);
        }
        catch (InvalidKeyException e) {
            throw new UnsupportedOperationException(String.format("The given key of size '%s' is invalid", key.length),
                    e);
        }
    }

    public HmacSha256 update(byte[] bytes) {
        mac.update(bytes);

        return this;
    }

    public byte[] doFinal(byte[] bytes) {
        return mac.doFinal(bytes);
    }
}
