package de.slackspace.openkeepass.domain;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class KeepassHeader {

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
			case 2:	setCipher(value);
			break;
			case 3: setCompressionFlag(value);
			break;
			case 4: setMasterSeed(value);
			break;
			case 5: setTransformSeed(value);
			break;
			case 6: setTransformRounds(value);
			break;
			case 7: setEncryptionIV(value);
			break;
			case 8: setProtectedStreamKey(value);
			break;
			case 9: setStreamStartBytes(value);
			break;
			case 10: setInnerRandomStreamId(value);
			break;
		}
	}

	private void setInnerRandomStreamId(byte[] value) {
		ByteBuffer buffer = wrapInBuffer(value);
		int intValue = buffer.getInt();
		crsAlgorithm = CrsAlgorithm.parseValue(intValue);
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
		transformRounds  = buffer.getLong();
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

	public byte[] getEncryptionIV() {
		return encryptionIV;
	}

	public byte[] getStreamStartBytes() {
		return streamStartBytes;
	}

	public CrsAlgorithm getCrsAlgorithm() {
		return crsAlgorithm;
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
}
