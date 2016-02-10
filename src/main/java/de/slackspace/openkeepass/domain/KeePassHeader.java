package de.slackspace.openkeepass.domain;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.slackspace.openkeepass.crypto.RandomGenerator;
import de.slackspace.openkeepass.exception.KeePassHeaderUnreadableException;
import de.slackspace.openkeepass.stream.SafeInputStream;
import de.slackspace.openkeepass.util.ByteUtils;

public class KeePassHeader {

	private static final int SIZE_OF_FIELD_LENGTH_BUFFER = 3;

	// Header Fields
	public static final int CIPHER = 2;
	public static final int COMPRESSION = 3;
	public static final int MASTER_SEED = 4;
	public static final int TRANSFORM_SEED = 5;
	public static final int TRANSFORM_ROUNDS = 6;
	public static final int ENCRYPTION_IV = 7;
	public static final int PROTECTED_STREAM_KEY = 8;
	public static final int STREAM_START_BYTES = 9;
	public static final int INNER_RANDOM_STREAM_ID = 10;

	// KeePass 2.x signature
	private static final byte[] DATABASE_V2_FILE_SIGNATURE_1 = ByteUtils.hexStringToByteArray("03d9a29a");
	private static final byte[] DATABASE_V2_FILE_SIGNATURE_2 = ByteUtils.hexStringToByteArray("67fb4bb5");
	private static final byte[] DATABASE_V2_FILE_VERSION = ByteUtils.hexStringToByteArray("00000300");

	// KeePass Magic Bytes for AES Cipher
	private static final byte[] DATABASE_V2_AES_CIPHER = ByteUtils
			.hexStringToByteArray("31C1F2E6BF714350BE5805216AFC5AFF");

	// KeePass version signature length in bytes
	public static final int VERSION_SIGNATURE_LENGTH = 12;

	// KeePass 2.x signature
	private static final int DATABASE_V2_FILE_SIGNATURE_1_INT = 0x9AA2D903 & 0xFF;
	private static final int DATABASE_V2_FILE_SIGNATURE_2_INT = 0xB54BFB67 & 0xFF;

	// KeePass 1.x signature
	private static final int OLD_DATABASE_V1_FILE_SIGNATURE_1_INT = 0x9AA2D903 & 0xFF;
	private static final int OLD_DATABASE_V1_FILE_SIGNATURE_2_INT = 0xB54BFB65 & 0xFF;

	private byte[] cipher;
	private byte[] encryptionIV;
	private byte[] streamStartBytes;
	private byte[] masterSeed;
	private byte[] transformSeed;
	private byte[] protectedStreamKey;
	private CompressionAlgorithm compression;
	private long transformRounds;
	private CrsAlgorithm crsAlgorithm;

	public void setValue(int headerId, byte[] value) {
		switch (headerId) {
		case CIPHER:
			setCipher(value);
			break;
		case COMPRESSION:
			setCompressionFlag(value);
			break;
		case MASTER_SEED:
			setMasterSeed(value);
			break;
		case TRANSFORM_SEED:
			setTransformSeed(value);
			break;
		case TRANSFORM_ROUNDS:
			setTransformRounds(value);
			break;
		case ENCRYPTION_IV:
			setEncryptionIV(value);
			break;
		case PROTECTED_STREAM_KEY:
			setProtectedStreamKey(value);
			break;
		case STREAM_START_BYTES:
			setStreamStartBytes(value);
			break;
		case INNER_RANDOM_STREAM_ID:
			setInnerRandomStreamId(value);
			break;
		}
	}

	public void checkVersionSupport(byte[] keepassFile) throws IOException {
		BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(keepassFile));

		byte[] signature = new byte[VERSION_SIGNATURE_LENGTH];
		int readBytes = inputStream.read(signature);
		if(readBytes == -1) {
			throw new UnsupportedOperationException("Could not read KeePass header. The provided file seems to be no KeePass database file!");
		}

		ByteBuffer signatureBuffer = ByteBuffer.wrap(signature);
		signatureBuffer.order(ByteOrder.LITTLE_ENDIAN);

		int signaturePart1 = ByteUtils.toUnsignedInt(signatureBuffer.getInt());
		int signaturePart2 = ByteUtils.toUnsignedInt(signatureBuffer.getInt());

		if (signaturePart1 == DATABASE_V2_FILE_SIGNATURE_1_INT && signaturePart2 == DATABASE_V2_FILE_SIGNATURE_2_INT) {
			return;
		} else if (signaturePart1 == OLD_DATABASE_V1_FILE_SIGNATURE_1_INT
				&& signaturePart2 == OLD_DATABASE_V1_FILE_SIGNATURE_2_INT) {
			throw new UnsupportedOperationException(
					"The provided KeePass database file seems to be from KeePass 1.x which is not supported!");
		} else {
			throw new UnsupportedOperationException("The provided file seems to be no KeePass database file!");
		}
	}

	/**
	 * Initializes the header values from a given byte array.
	 * 
	 * @param keepassFile
	 *            the byte array to read from
	 * @throws IOException
	 *             if the header cannot be read
	 */
	@SuppressWarnings("resource")
	public void read(byte[] keepassFile) throws IOException {
		SafeInputStream inputStream = new SafeInputStream(new BufferedInputStream(new ByteArrayInputStream(keepassFile)));
		inputStream.skipSafe(VERSION_SIGNATURE_LENGTH); // skip version

		while (true) {
			try {
				int fieldId = inputStream.read();
				byte[] fieldLength = new byte[2];
				inputStream.readSafe(fieldLength);

				ByteBuffer fieldLengthBuffer = ByteBuffer.wrap(fieldLength);
				fieldLengthBuffer.order(ByteOrder.LITTLE_ENDIAN);
				int fieldLengthInt = ByteUtils.toUnsignedInt(fieldLengthBuffer.getShort());

				if (fieldLengthInt > 0) {
					byte[] data = new byte[fieldLengthInt];
					inputStream.readSafe(data);
					setValue(fieldId, data);
				}

				if (fieldId == 0) {
					break;
				}
			} catch (IOException e) {
				throw new KeePassHeaderUnreadableException("Could not read header input", e);
			}
		}
	}

	/**
	 * Returns the whole header as byte array.
	 * 
	 * @return header as byte array
	 */
	public byte[] getBytes() {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			stream.write(DATABASE_V2_FILE_SIGNATURE_1);
			stream.write(DATABASE_V2_FILE_SIGNATURE_2);
			stream.write(DATABASE_V2_FILE_VERSION);

			for (int i = 2; i < 11; i++) {
				byte[] headerValue = getValue(i);

				// Write index
				stream.write(i);

				// Write length
				byte[] length = new byte[] { (byte) headerValue.length, 0 };
				stream.write(length);

				// Write value
				stream.write(headerValue);
			}

			// Write terminating flag
			stream.write(getEndOfHeader());

			return stream.toByteArray();
		} catch (IOException e) {
			throw new KeePassHeaderUnreadableException("Could not write header value to stream", e);
		}
	}

	private byte[] getEndOfHeader() {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(0);
			stream.write(4);
			stream.write(0);
			stream.write("\r\n\r\n".getBytes());

			return stream.toByteArray();
		} catch (IOException e) {
			throw new KeePassHeaderUnreadableException("Could not write end of header to stream", e);
		}
	}

	public byte[] getValue(int headerId) {
		switch (headerId) {
		case CIPHER:
			return getCipher();
		case COMPRESSION:
			return getCompressionFlag();
		case MASTER_SEED:
			return getMasterSeed();
		case TRANSFORM_SEED:
			return getTransformSeed();
		case TRANSFORM_ROUNDS:
			return getTransformRoundsByte();
		case ENCRYPTION_IV:
			return getEncryptionIV();
		case PROTECTED_STREAM_KEY:
			return getProtectedStreamKey();
		case STREAM_START_BYTES:
			return getStreamStartBytes();
		case INNER_RANDOM_STREAM_ID:
			return getInnerRandomStreamId();
		}

		return new byte[0];
	}

	private void setInnerRandomStreamId(byte[] value) {
		ByteBuffer buffer = wrapInBuffer(value);
		int intValue = buffer.getInt();
		crsAlgorithm = CrsAlgorithm.parseValue(intValue);
	}

	private byte[] getInnerRandomStreamId() {
		int intValue = CrsAlgorithm.getIntValue(crsAlgorithm);
		return wrapInBuffer(intValue);
	}

	private void setStreamStartBytes(byte[] value) {
		streamStartBytes = value;
	}

	private void setProtectedStreamKey(byte[] value) {
		protectedStreamKey = value;
	}

	private void setEncryptionIV(byte[] value) {
		encryptionIV = value;
	}

	private void setTransformRounds(byte[] value) {
		ByteBuffer buffer = wrapInBuffer(value);
		transformRounds = buffer.getLong();
	}

	private void setTransformSeed(byte[] value) {
		transformSeed = value;
	}

	private void setMasterSeed(byte[] value) {
		masterSeed = value;
	}

	private void setCompressionFlag(byte[] value) {
		ByteBuffer buffer = wrapInBuffer(value);
		int intValue = buffer.getInt();

		compression = CompressionAlgorithm.parseValue(intValue);
	}

	public void setCompression(CompressionAlgorithm algorithm) {
		compression = algorithm;
	}

	private byte[] getCompressionFlag() {
		int intValue = CompressionAlgorithm.getIntValue(compression);
		return wrapInBuffer(intValue);
	}

	private void setCipher(byte[] value) {
		if (value == null || value.length != 16) {
			throw new IllegalArgumentException("The encryption cipher must contain 16 bytes!");
		}
		cipher = value;
	}

	public byte[] getCipher() {
		return cipher;
	}

	public CompressionAlgorithm getCompression() {
		return compression;
	}

	public long getTransformRounds() {
		return transformRounds;
	}

	public void setTransformRounds(long rounds) {
		transformRounds = rounds;
	}

	private byte[] getTransformRoundsByte() {
		return wrapInBuffer(transformRounds);
	}

	public byte[] getEncryptionIV() {
		return encryptionIV;
	}

	public byte[] getStreamStartBytes() {
		return streamStartBytes;
	}

	public CrsAlgorithm getCrsAlgorithm() {
		return crsAlgorithm;
	}

	public void setCrsAlgorithm(CrsAlgorithm algorithm) {
		crsAlgorithm = algorithm;
	}

	public byte[] getMasterSeed() {
		return masterSeed;
	}

	public byte[] getTransformSeed() {
		return transformSeed;
	}

	public int getHeaderSize() {
		int size = 0;

		// Add size of values
		for (int i = 2; i < 11; i++) {
			byte[] value = getValue(i);

			if (value != null) {
				size += value.length + SIZE_OF_FIELD_LENGTH_BUFFER;
			}
		}

		// Add size of header end
		size += getEndOfHeader().length;

		return size;
	}

	public byte[] getProtectedStreamKey() {
		return protectedStreamKey;
	}

	private ByteBuffer wrapInBuffer(byte[] value) {
		ByteBuffer buffer = ByteBuffer.wrap(value);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer;
	}

	private byte[] wrapInBuffer(int value) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(value);
		return buffer.array();
	}

	private byte[] wrapInBuffer(long value) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putLong(value);
		return buffer.array();
	}

	/**
	 * Initializes the header with default values and creates new random values
	 * for crypto keys.
	 * <p>
	 * Default values:
	 * <ul>
	 * <li>Compression: GZIP</li>
	 * <li>CrsAlgorithm: Salsa20</li>
	 * <li>TransformRounds: 8000
	 * <li>
	 * <li>Cipher: AES</li>
	 * </ul>
	 */
	public void initialize() {
		RandomGenerator random = new RandomGenerator();

		setCompression(CompressionAlgorithm.Gzip);
		setCrsAlgorithm(CrsAlgorithm.Salsa20);
		setTransformRounds(8000);
		setMasterSeed(random.getRandomBytes(32));
		setTransformSeed(random.getRandomBytes(32));
		setEncryptionIV(random.getRandomBytes(16));
		setProtectedStreamKey(random.getRandomBytes(32));
		setStreamStartBytes(random.getRandomBytes(32));
		setCipher(DATABASE_V2_AES_CIPHER);
	}
}
