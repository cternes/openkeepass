package de.slackspace.openkeepass.crypto;

import de.slackspace.openkeepass.domain.KdfDictionary;
import de.slackspace.openkeepass.domain.KeePassHeader;

public class CryptoInformation {

    private byte[] encryptionIv;

    private KeePassHeader header;

    public CryptoInformation(KeePassHeader header) {
        this.header = header;
        this.encryptionIv = header.getEncryptionIV();
    }

    public boolean isV4Format() {
        return header.isV4Format();
    }

    public byte[] getHMACKey(byte[] hashedPassword) {
        return header.getHMACKey(hashedPassword);
    }

    public byte[] getMasterSeed() {
        return header.getMasterSeed();
    }

    public byte[] getTransformSeed() {
        return header.getTransformSeed();
    }

    public long getTransformRounds() {
        return header.getTransformRounds();
    }

    public int getHeaderSize() {
        return header.getHeaderSize();
    }

    public byte[] getEncryptionIV() {
        return encryptionIv;
    }

    public int getVersionSignatureLength() {
        return KeePassHeader.VERSION_SIGNATURE_LENGTH;
    }

    public KdfDictionary getKdfParameters() {
        return header.getKdfParameters();
    }

}
