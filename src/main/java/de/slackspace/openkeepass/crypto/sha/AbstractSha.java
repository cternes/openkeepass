package de.slackspace.openkeepass.crypto.sha;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class AbstractSha {

    private MessageDigest md;

    public AbstractSha(MessageDigest md) {
        this.md = md;
    }

    public byte[] hash(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text must not be null");
        }

        return hash(text.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] hash(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes must not be null");
        }

        return hash(bytes, 0, bytes.length);
    }

    public byte[] hash(byte[] bytes, int offset, int length) {
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes must not be null");
        }

        md.update(bytes, offset, length);
        return md.digest();
    }

    public AbstractSha update(byte[] bytes) {
        md.update(bytes);

        return this;
    }

    protected static MessageDigest getDigestInstance(ShaAlgorithm algorithm) {
        try {
            return MessageDigest.getInstance(algorithm.getName());
        }
        catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(
                    String.format("The algorithm '%s' is not supported", algorithm.getName()), e);
        }
    }
}
