package de.slackspace.openkeepass.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256 {

    private Sha256() {
    }

    public static byte[] hash(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text must not be null");
        }

        try {
            return hash(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("The encoding 'UTF-8' is not supported", e);
        }
    }

    public static byte[] hash(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes must not be null");
        }

        return hash(bytes, 0, bytes.length);
    }

    public static byte[] hash(byte[] bytes, int offset, int length) {
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes must not be null");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes, offset, length);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("The algorithm 'SHA-256' is not supported", e);
        }
    }
}
