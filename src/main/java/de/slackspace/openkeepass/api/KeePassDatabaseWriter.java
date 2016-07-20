package de.slackspace.openkeepass.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPOutputStream;

import de.slackspace.openkeepass.crypto.CryptoInformation;
import de.slackspace.openkeepass.crypto.Decrypter;
import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;
import de.slackspace.openkeepass.crypto.RandomGenerator;
import de.slackspace.openkeepass.crypto.Salsa20;
import de.slackspace.openkeepass.crypto.Sha256;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassHeader;
import de.slackspace.openkeepass.domain.zipper.GroupZipper;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnwriteableException;
import de.slackspace.openkeepass.parser.KeePassDatabaseXmlParser;
import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.processor.EncryptionStrategy;
import de.slackspace.openkeepass.processor.ProtectedValueProcessor;
import de.slackspace.openkeepass.stream.HashedBlockOutputStream;

public class KeePassDatabaseWriter {

    private static final String UTF_8 = "UTF-8";

    public void writeKeePassFile(KeePassFile keePassFile, String password, OutputStream stream) {
        try {
            if (!validateKeePassFile(keePassFile)) {
                throw new KeePassDatabaseUnwriteableException(
                        "The provided keePassFile is not valid. A valid keePassFile must contain of meta and root group and the root group must at least contain one group.");
            }

            KeePassHeader header = new KeePassHeader(new RandomGenerator());
            byte[] hashedPassword = hashPassword(password);

            byte[] keePassFilePayload = marshallXml(keePassFile, header);
            ByteArrayOutputStream streamToZip = compressStream(keePassFilePayload);
            ByteArrayOutputStream streamToHashBlock = hashBlockStream(streamToZip);
            ByteArrayOutputStream streamToEncrypt = combineHeaderAndContent(header, streamToHashBlock);
            byte[] encryptedDatabase = encryptStream(header, hashedPassword, streamToEncrypt);

            // Write database to stream
            stream.write(encryptedDatabase);
        } catch (IOException e) {
            throw new KeePassDatabaseUnwriteableException("Could not write database file", e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    private byte[] hashPassword(String password) throws UnsupportedEncodingException {
        byte[] passwordBytes = password.getBytes(UTF_8);
        return Sha256.hash(passwordBytes);
    }

    private byte[] encryptStream(KeePassHeader header, byte[] hashedPassword, ByteArrayOutputStream streamToEncrypt) throws IOException {
        CryptoInformation cryptoInformation = new CryptoInformation(KeePassHeader.VERSION_SIGNATURE_LENGTH, header.getMasterSeed(), header.getTransformSeed(),
                header.getEncryptionIV(), header.getTransformRounds(), header.getHeaderSize());

        return new Decrypter().encryptDatabase(hashedPassword, cryptoInformation, streamToEncrypt.toByteArray());
    }

    private ByteArrayOutputStream combineHeaderAndContent(KeePassHeader header, ByteArrayOutputStream content) throws IOException {
        ByteArrayOutputStream streamToEncrypt = new ByteArrayOutputStream();
        streamToEncrypt.write(header.getBytes());
        streamToEncrypt.write(header.getStreamStartBytes());
        streamToEncrypt.write(content.toByteArray());
        return streamToEncrypt;
    }

    private ByteArrayOutputStream hashBlockStream(ByteArrayOutputStream streamToUnzip) throws IOException {
        ByteArrayOutputStream streamToHashBlock = new ByteArrayOutputStream();
        HashedBlockOutputStream hashBlockOutputStream = new HashedBlockOutputStream(streamToHashBlock);
        hashBlockOutputStream.write(streamToUnzip.toByteArray());
        hashBlockOutputStream.close();
        return streamToHashBlock;
    }

    private byte[] marshallXml(KeePassFile keePassFile, KeePassHeader header) {
        KeePassFile clonedKeePassFile = new GroupZipper(keePassFile).cloneKeePassFile();

        ProtectedStringCrypto protectedStringCrypto = Salsa20.createInstance(header.getProtectedStreamKey());
        new ProtectedValueProcessor().processProtectedValues(new EncryptionStrategy(protectedStringCrypto), clonedKeePassFile);

        return new KeePassDatabaseXmlParser(new SimpleXmlParser()).toXml(keePassFile).toByteArray();
    }

    private ByteArrayOutputStream compressStream(byte[] keePassFilePayload) throws IOException {
        ByteArrayOutputStream streamToZip = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(streamToZip);
        gzipOutputStream.write(keePassFilePayload);
        gzipOutputStream.close();
        return streamToZip;
    }

    private static boolean validateKeePassFile(KeePassFile keePassFile) {
        if (keePassFile == null || keePassFile.getMeta() == null) {
            return false;
        }

        if (keePassFile.getRoot() == null || keePassFile.getRoot().getGroups().isEmpty()) {
            return false;
        }

        return true;
    }
}
