package de.slackspace.openkeepass;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.bouncycastle.util.encoders.Base64;

import de.slackspace.openkeepass.crypto.Decrypter;
import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;
import de.slackspace.openkeepass.crypto.Salsa20;
import de.slackspace.openkeepass.crypto.Sha256;
import de.slackspace.openkeepass.domain.CompressionAlgorithm;
import de.slackspace.openkeepass.domain.CrsAlgorithm;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassHeader;
import de.slackspace.openkeepass.domain.KeyFile;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadable;
import de.slackspace.openkeepass.parser.KeePassDatabaseXmlParser;
import de.slackspace.openkeepass.parser.KeyFileXmlParser;
import de.slackspace.openkeepass.stream.HashedBlockInputStream;
import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.util.StreamUtils;

/**
 * A KeePassDatabase is the central API class to read a KeePass database file.
 * <p>
 * Currently the following KeePass files are supported:
 * 
 * 	<ul>
 * 		<li>KeePass Database V2 with password</li>
 * 		<li>KeePass Database V2 with keyfile</li>
 * </ul>
 * 
 * A typical use-case should use the following idiom:
 * <pre>
 * // open database 
 * KeePassFile database = KeePassDatabase.getInstance("keePassDatabasePath").openDatabase("secret");
 * 
 * // get password entries 
 * List&lt;Entry&gt; entries = database.getEntries();
 * ...
 * </pre>
 * 
 * If the database could not be opened a <tt>RuntimeException</tt> will be thrown.
 * 
 * @see KeePassFile
 * 
 */
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
	private byte[] keepassFile;
	
	protected Decrypter decrypter = new Decrypter();
	protected KeePassDatabaseXmlParser keePassDatabaseXmlParser = new KeePassDatabaseXmlParser();
	protected KeyFileXmlParser keyFileXmlParser = new KeyFileXmlParser();
	
	private KeePassDatabase(InputStream inputStream) {
		try {
			keepassFile = StreamUtils.toByteArray(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Retrieves a KeePassDatabase instance. The instance returned is based on the given database filename and tries to parse the database header of it.
	 * 
	 * @param keePassDatabaseFile a KeePass database filename, must not be NULL 
	 * @return a KeePassDatabase
	 */
	public static KeePassDatabase getInstance(String keePassDatabaseFile) {
		return getInstance(new File(keePassDatabaseFile));
	}
	
	/**
	 * Retrieves a KeePassDatabase instance. The instance returned is based on the given database file and tries to parse the database header of it.
	 * 
	 * @param keePassDatabaseFile a KeePass database file, must not be NULL 
	 * @return a KeePassDatabase
	 */
	public static KeePassDatabase getInstance(File keePassDatabaseFile) {
		if(keePassDatabaseFile == null) {
			throw new IllegalArgumentException("You must provide a valid KeePass database file.");
		}
		
		try {
			return getInstance(new FileInputStream(keePassDatabaseFile));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("The KeePass database file could not be found. You must provide a valid KeePass database file.");
		}
	}
	
	/**
	 * Retrieves a KeePassDatabase instance. The instance returned is based on the given input stream and tries to parse the database header of it.
	 * 
	 * @param keePassDatabaseStream an input stream of a KeePass database, must not be NULL 
	 * @return a KeePassDatabase
	 */
	public static KeePassDatabase getInstance(InputStream keePassDatabaseStream) {
		if(keePassDatabaseStream == null) {
			throw new IllegalArgumentException("You must provide a non-empty KeePass database stream.");
		}
		
		KeePassDatabase reader = new KeePassDatabase(keePassDatabaseStream);

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

	/**
	 * Opens a KeePass database with the given password and returns the KeePassFile for further processing.
	 * <p>
	 * If the database cannot be decrypted with the provided password an exception will be thrown.
	 * 
	 * @param password the password to open the database
	 * @return a KeePassFile
	 * @see KeePassFile
	 */
	public KeePassFile openDatabase(String password) {
		if(password == null) {
			throw new IllegalArgumentException("The password for the database must not be null. Please provide a valid password.");
		}
		
		try {
			byte[] passwordBytes = password.getBytes("UTF-8");
			byte[] hashedPassword = Sha256.hash(passwordBytes);
			
			return decryptAndParseDatabase(hashedPassword);
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException("The encoding UTF-8 is not supported");
		}
	}
	
	/**
	 * Opens a KeePass database with the given password and returns the KeePassFile for further processing.
	 * <p>
	 * If the database cannot be decrypted with the provided password an exception will be thrown.
	 * 
	 * @param keyFile the password to open the database
	 * @return a KeePassFile the keyfile to open the database
	 * @see KeePassFile
	 */
	public KeePassFile openDatabase(File keyFile) {
		if(keyFile == null) {
			throw new IllegalArgumentException("You must provide a valid KeePass keyfile.");
		}
		
		try {
			return openDatabase(new FileInputStream(keyFile));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("The KeePass keyfile could not be found. You must provide a valid KeePass keyfile.");
		}
	}
	
	/**
	 * Opens a KeePass database with the given keyfile stream and returns the KeePassFile for further processing.
	 * <p>
	 * If the database cannot be decrypted with the provided keyfile an exception will be thrown. 
	 * 
	 * @param keyFileStream the keyfile to open the database as stream
	 * @return a KeePassFile
	 * @see KeePassFile
	 */
	public KeePassFile openDatabase(InputStream keyFileStream) {
		if(keyFileStream == null) {
			throw new IllegalArgumentException("You must provide a non-empty KeePass keyfile stream.");
		}
		
		try {
			KeyFile keyFile = keyFileXmlParser.parse(keyFileStream);
			byte[] protectedBuffer = Base64.decode(keyFile.getKey().getData().getBytes("UTF-8"));
			
			return decryptAndParseDatabase(protectedBuffer);
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException("The encoding UTF-8 is not supported");
		}
	}
	
	private KeePassFile decryptAndParseDatabase(byte[] key) {
		try {
			byte[] aesDecryptedDbFile = decrypter.decryptDatabase(key, keepassHeader, keepassFile);
			
			byte[] startBytes = new byte[32];
			ByteArrayInputStream decryptedStream = new ByteArrayInputStream(aesDecryptedDbFile);
			decryptedStream.read(startBytes);
			
			// compare startBytes
			if(!Arrays.equals(keepassHeader.getStreamStartBytes(), startBytes)) {
				throw new KeePassDatabaseUnreadable("The keepass database file seems to be corrupt or cannot be decrypted.");
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
			
			return keePassDatabaseXmlParser.parse(new ByteArrayInputStream(decompressed), protectedStringCrypto);
		} catch (IOException e) {
			throw new RuntimeException("Could not open database file", e);
		}
	}

	/**
	 * Gets the KeePassDatabase header.
	 * 
	 * @return the database header
	 */
	public KeePassHeader getHeader() {
		return keepassHeader;
	}
	
}
