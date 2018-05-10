package de.slackspace.openkeepass.crypto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.slackspace.openkeepass.crypto.sha.Sha256;
import de.slackspace.openkeepass.domain.KeePassHeader;
import de.slackspace.openkeepass.stream.HmacBlockInputStream;
import de.slackspace.openkeepass.util.SafeInputStream;
import de.slackspace.openkeepass.util.StreamUtils;

public class Decrypter {

    public byte[] decryptDatabase(byte[] password, CryptoInformation cryptoInformation, byte[] database) throws IOException {
        byte[] aesKey = createAesKey(password, cryptoInformation);

        return processDatabaseEncryption(false, password, database, cryptoInformation, aesKey);
    }

    public byte[] encryptDatabase(byte[] password, CryptoInformation cryptoInformation, byte[] database) throws IOException {
        byte[] aesKey = createAesKey(password, cryptoInformation);

        return processDatabaseEncryption(true, password, database, cryptoInformation, aesKey);
    }

    private byte[] processDatabaseEncryption(boolean encrypt, byte[] password, byte[] database,
            CryptoInformation cryptoInformation, byte[] aesKey) throws IOException {
        if (cryptoInformation.isV4Format()) {
            return processDatabaseEncryptionV4Format(encrypt, password, database, cryptoInformation, aesKey);
        }

        return processDatabaseEncryptionV3Format(encrypt, database, cryptoInformation, aesKey);
    }

    private byte[] processDatabaseEncryptionV4Format(boolean encrypt, byte[] password, byte[] database,
            CryptoInformation cryptoInformation,
            byte[] aesKey) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(database);
        inputStream.skip(KeePassHeader.VERSION_SIGNATURE_LENGTH + cryptoInformation.getHeaderSize());
        inputStream.skip(64);

        byte[] hmacKey = cryptoInformation.getHMACKey(password);
        HmacBlockInputStream hmacBlockInputStream = new HmacBlockInputStream(hmacKey, inputStream);

        byte[] hmacBytes = StreamUtils.toByteArray(hmacBlockInputStream);

        if (encrypt) {
            Aes.encrypt(aesKey, cryptoInformation.getEncryptionIV(), hmacBytes);
        }

        return Aes.decrypt(aesKey, cryptoInformation.getEncryptionIV(), hmacBytes);
    }

    private byte[] processDatabaseEncryptionV3Format(boolean encrypt, byte[] database,
            CryptoInformation cryptoInformation, byte[] aesKey) throws IOException {
        byte[] metaData = new byte[cryptoInformation.getVersionSignatureLength() +
                cryptoInformation.getHeaderSize()];
        SafeInputStream inputStream = new SafeInputStream(new BufferedInputStream(new ByteArrayInputStream(database)));
        inputStream.readSafe(metaData);

        byte[] payload = StreamUtils.toByteArray(inputStream);
        byte[] processedPayload;
        if (encrypt) {
            processedPayload = Aes.encrypt(aesKey, cryptoInformation.getEncryptionIV(), payload);
        }
        else {
            processedPayload = Aes.decrypt(aesKey, cryptoInformation.getEncryptionIV(), payload);
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(metaData);
        output.write(processedPayload);

        return output.toByteArray();
    }

    private byte[] createAesKey(byte[] password, CryptoInformation cryptoInformation) {
        byte[] hashedPwd = Sha256.getInstance().hash(password);
        if (cryptoInformation.isV4Format()) {
            return createAesKeyV4Format(hashedPwd, cryptoInformation);
        }

        return createAesKeyV3Format(hashedPwd, cryptoInformation);
    }

    private byte[] createAesKeyV3Format(byte[] password, CryptoInformation cryptoInformation) {
        byte[] transformedPwd = Aes.transformKey(cryptoInformation.getTransformSeed(), password,
                cryptoInformation.getTransformRounds());
        byte[] transformedHashedPwd = Sha256.getInstance().hash(transformedPwd);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(cryptoInformation.getMasterSeed(), 0, 32);
        stream.write(transformedHashedPwd, 0, 32);

        return Sha256.getInstance().hash(stream.toByteArray());
    }

    private byte[] createAesKeyV4Format(byte[] password, CryptoInformation cryptoInformation) {
        byte[] transformedKey = Argon2.transformKey(password, cryptoInformation.getKdfParameters());

        return Sha256.getInstance().update(cryptoInformation.getMasterSeed()).hash(transformedKey);
    }
}
