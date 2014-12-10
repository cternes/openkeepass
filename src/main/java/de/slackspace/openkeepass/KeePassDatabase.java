package de.slackspace.openkeepass;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import de.slackspace.openkeepass.crypto.Decrypter;
import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;
import de.slackspace.openkeepass.crypto.Salsa20;
import de.slackspace.openkeepass.domain.CompressionAlgorithm;
import de.slackspace.openkeepass.domain.CrsAlgorithm;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassHeader;
import de.slackspace.openkeepass.exception.KeepassDatabaseUnreadable;
import de.slackspace.openkeepass.parser.XmlParser;
import de.slackspace.openkeepass.stream.HashedBlockInputStream;
import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.util.StreamUtils;

public class KeePassDatabase {

	// KeePass 2.x signature
	private static final int DATABASE_V2_FILE_SIGNATURE_1 = 0x9AA2D903 & 0xFF;
	private static final int DATABASE_V2_FILE_SIGNATURE_2 = 0xB54BFB67 & 0xFF;
	
	// KeePass 1.x signature
	private static final int OLD_DATABASE_V1_FILE_SIGNATURE_1 = 0x9AA2D903 & 0xFF;
	private static final int OLD_DATABASE_V1_FILE_SIGNATURE_2 = 0xB54BFB65 & 0xFF;
	
	// KeePass version signature length in bytes 
	public static final int VERSION_SIGNATURE_LENGTH = 12;
	
	private KeePassHeader keepassHeader = new KeePassHeader();
	private Decrypter decrypter = new Decrypter();
	private XmlParser xmlParser = new XmlParser();
	private byte[] keepassFile;
	
	private KeePassDatabase(InputStream inputStream) {
		try {
			keepassFile = StreamUtils.toByteArray(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static KeePassDatabase getInstance(InputStream inputStream) {
		KeePassDatabase reader = new KeePassDatabase(inputStream);

		try {
			reader.checkVersionSupport();
			reader.readHeader();
			return reader;
		}
		catch(IOException e) {
			throw new RuntimeException("Could not read input stream", e);
		}
	}
	
	private void checkVersionSupport() throws IOException {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(keepassFile));

		byte[] signature = new byte[VERSION_SIGNATURE_LENGTH];
		bufferedInputStream.read(signature);
		ByteBuffer signatureBuffer = ByteBuffer.wrap(signature);
		signatureBuffer.order(ByteOrder.LITTLE_ENDIAN);

		int signaturePart1 = ByteUtils.toUnsignedInt(signatureBuffer.getInt());
		int signaturePart2 = ByteUtils.toUnsignedInt(signatureBuffer.getInt());

		if(signaturePart1 == DATABASE_V2_FILE_SIGNATURE_1 && signaturePart2 == DATABASE_V2_FILE_SIGNATURE_2) {
			return;
		}
		else if(signaturePart1 == OLD_DATABASE_V1_FILE_SIGNATURE_1 && signaturePart2 == OLD_DATABASE_V1_FILE_SIGNATURE_2) {
			throw new UnsupportedOperationException("The provided KeePass database file seems to be from KeePass 1.x which is not supported!");
		}
		else {
			throw new UnsupportedOperationException("The provided file seems to be no KeePass database file!");
		}
	}

	private void readHeader() throws IOException {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(keepassFile));
		bufferedInputStream.skip(VERSION_SIGNATURE_LENGTH); // skip version
		
		while(true) {
			try {
				int fieldId = bufferedInputStream.read();
				byte[] fieldLength = new byte[2];
				bufferedInputStream.read(fieldLength);
				
				ByteBuffer fieldLengthBuffer = ByteBuffer.wrap(fieldLength);
				fieldLengthBuffer.order(ByteOrder.LITTLE_ENDIAN);
				int fieldLengthInt = ByteUtils.toUnsignedInt(fieldLengthBuffer.getShort());

				if(fieldLengthInt > 0) {
					byte[] data = new byte[fieldLengthInt];
					bufferedInputStream.read(data);
					keepassHeader.setValue(fieldId, data);
					keepassHeader.increaseHeaderSize(fieldLengthInt + 3);
				}
				
				if(fieldId == 0) {
					break;
				}
			} catch (IOException e) {
				throw new RuntimeException("Could not read header input", e);
			}
		}
	}

	public KeePassFile openDatabase(String password) {
		try {
			byte[] aesDecryptedDbFile = decrypter.decryptDatabase(password, keepassHeader, keepassFile);
			
			byte[] startBytes = new byte[32];
			ByteArrayInputStream decryptedStream = new ByteArrayInputStream(aesDecryptedDbFile);
			decryptedStream.read(startBytes);
			
			// compare startBytes
			if(!Arrays.equals(keepassHeader.getStreamStartBytes(), startBytes)) {
				throw new KeepassDatabaseUnreadable("The keepass database file seems to be corrupt or cannot be decrypted.");
			}
			
			HashedBlockInputStream hashedBlockInputStream = new HashedBlockInputStream(decryptedStream);
			byte[] hashedBlockBytes = StreamUtils.toByteArray(hashedBlockInputStream);
			
			byte[] decompressed = hashedBlockBytes;
			
			// unzip if necessary
			if(keepassHeader.getCompression().equals(CompressionAlgorithm.Gzip)) {
				GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(hashedBlockBytes));
				decompressed = StreamUtils.toByteArray(gzipInputStream);
			}
			
			ProtectedStringCrypto protectedStringCrypto;
			if(keepassHeader.getCrsAlgorithm().equals(CrsAlgorithm.Salsa20)) {
				protectedStringCrypto = Salsa20.createInstance(keepassHeader.getProtectedStreamKey());
			}
			else {
				throw new UnsupportedOperationException("Only Salsa20 is supported as CrsAlgorithm at the moment!");
			}
			
			return xmlParser.parse(new ByteArrayInputStream(decompressed), protectedStringCrypto);
		} catch (IOException e) {
			throw new RuntimeException("Could not open database file", e);
		}
	}

	public KeePassHeader getHeader() {
		return keepassHeader;
	}
	
}
