package de.slackspace.openkeepass.crypto;

public class CryptoInformation {

    private int versionSignatureLength;

    private byte[] masterSeed;

    private byte[] transformSeed;

    private long transformRounds;

    private int headerSize;

    private byte[] encryptionIv;

    public CryptoInformation(int versionSignatureLength, byte[] masterSeed, byte[] transformSeed, byte[] encryptionIv, long transformRounds, int headerSize) {
        this.versionSignatureLength = versionSignatureLength;
        this.masterSeed = masterSeed;
        this.transformSeed = transformSeed;
        this.encryptionIv = encryptionIv;
        this.transformRounds = transformRounds;
        this.headerSize = headerSize;
    }

    public byte[] getMasterSeed() {
        return masterSeed;
    }

    public byte[] getTransformSeed() {
        return transformSeed;
    }

    public long getTransformRounds() {
        return transformRounds;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public byte[] getEncryptionIV() {
        return encryptionIv;
    }

    public int getVersionSignatureLength() {
        return versionSignatureLength;
    }

}
