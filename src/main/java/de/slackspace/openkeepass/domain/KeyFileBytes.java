package de.slackspace.openkeepass.domain;

public class KeyFileBytes {

    private boolean isReadable = false;

    private byte[] bytes;

    public KeyFileBytes(boolean isReadable, byte[] bytes) {
        this.isReadable = isReadable;
        this.bytes = bytes;
    }

    public boolean isReadable() {
        return isReadable;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
