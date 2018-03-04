package de.slackspace.openkeepass.domain;

public class KdfDictionary extends VariantDictionary {

    public KdfDictionary(byte[] bytes) {
        super(bytes);
    }

    public byte[] getUUID() {
        return getByteArray("$UUID");
    }

    public int getVersion() {
        return getInt("V");
    }

    public byte[] getSalt() {
        return getByteArray("S");
    }

    public long getMemory() {
        return getLong("M");
    }

    public long getIterations() {
        return getLong("I");
    }

    public byte[] getKey() {
        return getByteArray("K");
    }

    public byte[] getAssociatedData() {
        return getByteArray("A");
    }

    public int getParallelism() {
        return getInt("P");
    }
}
