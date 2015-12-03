package de.slackspace.openkeepass.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.slackspace.openkeepass.util.ByteUtils;

public class KeePassHeader {

	public static final int CIPHER = 2; 
	public static final int COMPRESSION = 3; 
	public static final int MASTER_SEED = 4; 
	public static final int TRANSFORM_SEED = 5; 
	public static final int TRANSFORM_ROUNDS = 6; 
	public static final int ENCRYPTION_IV = 7; 
	public static final int PROTECTED_STREAM_KEY = 8; 
	public static final int STREAM_START_BYTES = 9; 
	public static final int INNER_RANDOM_STREAM_ID = 10;
	
	private static final byte[] DATABASE_V2_FILE_SIGNATURE_1 = ByteUtils.hexStringToByteArray("03d9a29a");
	private static final byte[] DATABASE_V2_FILE_SIGNATURE_2 = ByteUtils.hexStringToByteArray("67fb4bb5");
	private static final byte[] DATABASE_V2_FILE_VERSION = ByteUtils.hexStringToByteArray("00000300");
	
	private int headerSize = 0;
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
			case CIPHER:	setCipher(value);
			break;
			case COMPRESSION: setCompressionFlag(value);
			break;
			case MASTER_SEED: setMasterSeed(value);
			break;
			case TRANSFORM_SEED: setTransformSeed(value);
			break;
			case TRANSFORM_ROUNDS: setTransformRounds(value);
			break;
			case ENCRYPTION_IV: setEncryptionIV(value);
			break;
			case PROTECTED_STREAM_KEY: setProtectedStreamKey(value);
			break;
			case STREAM_START_BYTES: setStreamStartBytes(value);
			break;
			case INNER_RANDOM_STREAM_ID: setInnerRandomStreamId(value);
			break;
		}
	}

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
			throw new RuntimeException("Could not write header value to stream", e);
		}
	}
	
	private byte[] getEndOfHeader() throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(0);
		stream.write(4);
		stream.write(0);
		stream.write("\r\n\r\n".getBytes());
		
		return stream.toByteArray();
	}

	public byte[] getValue(int headerId) {
		switch (headerId) {
			case CIPHER:	return getCipher();
			case COMPRESSION: return getCompressionFlag();
			case MASTER_SEED: return getMasterSeed();
			case TRANSFORM_SEED: return getTransformSeed();
			case TRANSFORM_ROUNDS: return getTransformRoundsByte();
			case ENCRYPTION_IV: return getEncryptionIV();
			case PROTECTED_STREAM_KEY: return getProtectedStreamKey();
			case STREAM_START_BYTES: return getStreamStartBytes();
			case INNER_RANDOM_STREAM_ID: return getInnerRandomStreamId();
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
		if(value == null || value.length != 16) {
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

	public void increaseHeaderSize(int numBytes) {
		headerSize += numBytes;
	}
	
	public int getHeaderSize() {
		return headerSize;
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
}
