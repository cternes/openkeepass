package de.slackspace.openkeepass.api;

import java.io.ByteArrayInputStream;
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
import de.slackspace.openkeepass.processor.DecryptionStrategy;
import de.slackspace.openkeepass.processor.IconEnricher;
import de.slackspace.openkeepass.processor.ProtectedValueProcessor;
import de.slackspace.openkeepass.stream.HashedBlockInputStream;
import de.slackspace.openkeepass.util.SafeInputStream;
import de.slackspace.openkeepass.util.StreamUtils;
import de.slackspace.openkeepass.xml.KeePassDatabaseXmlParser;

public class KeePassDatabaseReader {

	protected Decrypter decrypter = new Decrypter();
	protected KeePassDatabaseXmlParser keePassDatabaseXmlParser = new KeePassDatabaseXmlParser();

	private KeePassHeader keepassHeader;

	public KeePassDatabaseReader(KeePassHeader keepassHeader) {
		this.keepassHeader = keepassHeader;
	}

	public KeePassFile decryptAndParseDatabase(byte[] key, byte[] keepassFile) {
		try {
			byte[] aesDecryptedDbFile = decryptStream(key, keepassFile);
			SafeInputStream decryptedStream = skipMetadata(aesDecryptedDbFile);
			byte[] hashedBlockBytes = unHashBlockStream(decryptedStream);

			byte[] decompressed = decompressStream(hashedBlockBytes);
			ProtectedStringCrypto protectedStringCrypto = getProtectedStringCrypto();

			return parseDatabase(decompressed, protectedStringCrypto);
		} catch (IOException e) {
			throw new KeePassDatabaseUnreadableException("Could not open database file", e);
		}
	}

	private byte[] unHashBlockStream(SafeInputStream decryptedStream) throws IOException {
		HashedBlockInputStream hashedBlockInputStream = new HashedBlockInputStream(decryptedStream);
		byte[] hashedBlockBytes = StreamUtils.toByteArray(hashedBlockInputStream);
		return hashedBlockBytes;
	}

	private KeePassFile parseDatabase(byte[] decompressed, ProtectedStringCrypto protectedStringCrypto) {
		KeePassFile unprocessedKeepassFile = keePassDatabaseXmlParser.fromXml(new ByteArrayInputStream(decompressed));
		new ProtectedValueProcessor().processProtectedValues(new DecryptionStrategy(protectedStringCrypto), unprocessedKeepassFile);

		return new IconEnricher().enrichNodesWithIconData(unprocessedKeepassFile);
	}

	private ProtectedStringCrypto getProtectedStringCrypto() {
		ProtectedStringCrypto protectedStringCrypto;
		if (keepassHeader.getCrsAlgorithm().equals(CrsAlgorithm.Salsa20)) {
			protectedStringCrypto = Salsa20.createInstance(keepassHeader.getProtectedStreamKey());
		} else {
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

	private byte[] decryptStream(byte[] key, byte[] keepassFile) throws IOException {
		CryptoInformation cryptoInformation = new CryptoInformation(KeePassHeader.VERSION_SIGNATURE_LENGTH, keepassHeader.getMasterSeed(),
				keepassHeader.getTransformSeed(), keepassHeader.getEncryptionIV(), keepassHeader.getTransformRounds(), keepassHeader.getHeaderSize());
		return decrypter.decryptDatabase(key, cryptoInformation, keepassFile);
	}
}
