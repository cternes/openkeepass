package de.slackspace.openkeepass.crypto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.slackspace.openkeepass.util.SafeInputStream;
import de.slackspace.openkeepass.util.StreamUtils;

public class Decrypter {

    public byte[] decryptDatabase(byte[] password, CryptoInformation cryptoInformation, byte[] database) throws IOException {
        byte[] aesKey = createAesKey(password, cryptoInformation);

        return processDatabaseEncryption(false, database, cryptoInformation, aesKey);
    }

    public byte[] encryptDatabase(byte[] password, CryptoInformation cryptoInformation, byte[] database) throws IOException {
        byte[] aesKey = createAesKey(password, cryptoInformation);

        return processDatabaseEncryption(true, database, cryptoInformation, aesKey);
    }

    private byte[] processDatabaseEncryption(boolean encrypt, byte[] database, CryptoInformation cryptoInformation, byte[] aesKey) throws IOException {
        byte[] metaData = new byte[cryptoInformation.getVersionSignatureLength() + cryptoInformation.getHeaderSize()];
        SafeInputStream inputStream = new SafeInputStream(new BufferedInputStream(new ByteArrayInputStream(database)));
        inputStream.readSafe(metaData);

        byte[] payload = StreamUtils.toByteArray(inputStream);
        byte[] processedPayload;
        if (encrypt) {
            processedPayload = Aes.encrypt(aesKey, cryptoInformation.getEncryptionIV(), payload);
        } else {
            processedPayload = Aes.decrypt(aesKey, cryptoInformation.getEncryptionIV(), payload);
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(metaData);
        output.write(processedPayload);

        return output.toByteArray();
    }

    private byte[] createAesKey(byte[] password, CryptoInformation cryptoInformation) {
        byte[] hashedPwd = Sha256.hash(password);

        byte[] transformedPwd = Aes.transformKey(cryptoInformation.getTransformSeed(), hashedPwd, cryptoInformation.getTransformRounds());
        byte[] transformedHashedPwd = Sha256.hash(transformedPwd);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(cryptoInformation.getMasterSeed(), 0, 32);
        stream.write(transformedHashedPwd, 0, 32);

        return Sha256.hash(stream.toByteArray());
    }
}
