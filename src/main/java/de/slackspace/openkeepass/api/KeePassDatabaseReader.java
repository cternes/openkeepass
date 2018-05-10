package de.slackspace.openkeepass.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import de.slackspace.openkeepass.crypto.CryptoInformation;
import de.slackspace.openkeepass.crypto.Decrypter;
import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;
import de.slackspace.openkeepass.crypto.Salsa20;
import de.slackspace.openkeepass.domain.CompressionAlgorithm;
import de.slackspace.openkeepass.domain.CrsAlgorithm;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassHeader;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException;
import de.slackspace.openkeepass.parser.KeePassDatabaseXmlParser;
import de.slackspace.openkeepass.parser.SimpleXmlParser;
import de.slackspace.openkeepass.processor.DecryptionStrategy;
import de.slackspace.openkeepass.processor.Enricher;
import de.slackspace.openkeepass.processor.ProtectedValueProcessor;
import de.slackspace.openkeepass.stream.HashedBlockInputStream;
import de.slackspace.openkeepass.util.SafeInputStream;
import de.slackspace.openkeepass.util.StreamUtils;

public class KeePassDatabaseReader {

    protected Decrypter decrypter = new Decrypter();
    protected KeePassDatabaseXmlParser keePassDatabaseXmlParser = new KeePassDatabaseXmlParser(new SimpleXmlParser());

    private KeePassHeader keepassHeader;

    public KeePassDatabaseReader(KeePassHeader keepassHeader) {
        this.keepassHeader = keepassHeader;
    }

    public KeePassFile decryptAndParseDatabase(byte[] key, byte[] keepassFile) {
        try {
            if (keepassHeader.isV4Format()) {
                return handleV4Format(key, keepassFile);
            }

            return handleV3Format(key, keepassFile);
        }
        catch (IOException e) {
            throw new KeePassDatabaseUnreadableException("Could not open database file", e);
        }
    }

    private KeePassFile handleV3Format(byte[] key, byte[] keepassFile) throws IOException {
        byte[] aesDecryptedDbFile = decryptStream(key, keepassFile, keepassHeader);
        SafeInputStream decryptedStream = skipMetadata(aesDecryptedDbFile);
        byte[] hashedBlockBytes = unHashBlockStream(decryptedStream);

        byte[] decompressed = decompressStream(hashedBlockBytes);

        ProtectedStringCrypto protectedStringCrypto = getProtectedStringCrypto();

        return parseDatabase(decompressed, protectedStringCrypto);
    }

    private KeePassFile handleV4Format(byte[] key, byte[] keepassFile) throws IOException {
        byte[] aesDecryptedDbFile = decryptStream(key, keepassFile, keepassHeader);
        byte[] decompressed = decompressStream(aesDecryptedDbFile);

        keepassHeader.readInner(decompressed);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int skipBytes = keepassHeader.getInnerHeaderSize();
        output.write(decompressed, skipBytes, decompressed.length - skipBytes);

        // TODO: implement
        ProtectedStringCrypto protectedStringCrypto = new ProtectedStringCrypto() {

            @Override
            public String encrypt(String plainString) {
                return null;
            }

            @Override
            public String decrypt(String protectedString) {
                return null;
            }
        };
        // System.out.println("TEST:" + new String(output.toByteArray()));
        return parseDatabase(output.toByteArray(), protectedStringCrypto);
    }

    private byte[] unHashBlockStream(SafeInputStream decryptedStream) throws IOException {
        HashedBlockInputStream hashedBlockInputStream = new HashedBlockInputStream(decryptedStream);
        return StreamUtils.toByteArray(hashedBlockInputStream);
    }

    private KeePassFile parseDatabase(byte[] decompressed, ProtectedStringCrypto protectedStringCrypto) {
        KeePassFile unprocessedKeepassFile = keePassDatabaseXmlParser.fromXml(new ByteArrayInputStream(decompressed));
        new ProtectedValueProcessor().processProtectedValues(new DecryptionStrategy(protectedStringCrypto),
                unprocessedKeepassFile);

        return new Enricher(unprocessedKeepassFile)
                .enrichIcons()
                .enrichAttachments()
                .enrichReferences()
                .process();
    }

    private ProtectedStringCrypto getProtectedStringCrypto() {
        ProtectedStringCrypto protectedStringCrypto;
        if (keepassHeader.getCrsAlgorithm().equals(CrsAlgorithm.Salsa20)) {
            protectedStringCrypto = Salsa20.createInstance(keepassHeader.getProtectedStreamKey());
        }
        else {
            throw new UnsupportedOperationException("Only Salsa20 is supported as CrsAlgorithm at the moment!");
        }
        return protectedStringCrypto;
    }

    private byte[] decompressStream(byte[] hashedBlockBytes) throws IOException {
        byte[] decompressed = hashedBlockBytes;

        // Unzip if necessary
        if (keepassHeader.getCompression().equals(CompressionAlgorithm.Gzip)) {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(hashedBlockBytes));
            decompressed = StreamUtils.toByteArray(gzipInputStream);
        }

        return decompressed;
    }

    private SafeInputStream skipMetadata(byte[] aesDecryptedDbFile) throws IOException {
        SafeInputStream decryptedStream = new SafeInputStream(new ByteArrayInputStream(aesDecryptedDbFile));

        byte[] startBytes = new byte[32];

        // Metadata must be skipped here
        decryptedStream.skipSafe(KeePassHeader.VERSION_SIGNATURE_LENGTH + keepassHeader.getHeaderSize());
        decryptedStream.readSafe(startBytes);

        // Compare startBytes
        if (!Arrays.equals(keepassHeader.getStreamStartBytes(), startBytes)) {
            throw new KeePassDatabaseUnreadableException(
                    "The keepass database file seems to be corrupt or cannot be decrypted.");
        }

        return decryptedStream;
    }

    private byte[] decryptStream(byte[] key, byte[] keepassFile, KeePassHeader header) throws IOException {
        CryptoInformation cryptoInformation = new CryptoInformation(header);
        return decrypter.decryptDatabase(key, cryptoInformation, keepassFile);
    }
}
