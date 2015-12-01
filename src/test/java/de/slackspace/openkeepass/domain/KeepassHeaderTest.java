package de.slackspace.openkeepass.domain;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.util.ByteUtils;

public class KeepassHeaderTest {

	@Test(expected=IllegalArgumentException.class)
	public void whenCipherIsSetToInvalidValueShouldThrowException() {
		createHeaderAndSetValue(2, new byte[1]);
	}
	
	@Test
	public void whenCipherIsSetToByteArrayShouldReturnHeaderWithCipher() {
		KeePassHeader header = createHeaderAndSetValue(2, ByteUtils.hexStringToByteArray("31C1F2E6BF714350BE5805216AFC5AFF"));
		Assert.assertTrue("Cipher must be 31C1F2E6BF714350BE5805216AFC5AFF", Arrays.equals(ByteUtils.hexStringToByteArray("31C1F2E6BF714350BE5805216AFC5AFF"), header.getCipher()));
	}
	
	@Test(expected=BufferUnderflowException.class)
	public void whenCompressionIsSetToInvalidValueShouldThrowException() {
		createHeaderAndSetValue(3, new byte[2]);
	}
	
	@Test
	public void whenCompressionIsSetGzipShouldReturnHeaderWithGzipCompression() {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(0x01000000);
		KeePassHeader header = createHeaderAndSetValue(3, b.array());
		Assert.assertEquals(CompressionAlgorithm.Gzip, header.getCompression());
	}
	
	@Test
	public void whenCompressionIsSetNoneShouldReturnHeaderWithNoneCompression() {
		KeePassHeader header = createHeaderAndSetValue(3, new byte[4]);
		Assert.assertEquals(CompressionAlgorithm.None, header.getCompression());
	}
	
	@Test
	public void whenCrsIsSetArcFourShouldReturnHeaderWithArgFourCrsAlgorithm() {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(0x01000000);
		KeePassHeader header = createHeaderAndSetValue(10, b.array());
		Assert.assertEquals(CrsAlgorithm.ArcFourVariant, header.getCrsAlgorithm());
	}
	
	@Test
	public void whenCrsIsSetSalsa20ShouldReturnHeaderWithSalsa20CrsAlgorithm() {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(0x02000000);
		KeePassHeader header = createHeaderAndSetValue(10, b.array());
		Assert.assertEquals(CrsAlgorithm.Salsa20, header.getCrsAlgorithm());
	}
	
	@Test
	public void whenCrsIsSetNullShouldReturnHeaderWithNullCrsAlgorithm() {
		KeePassHeader header = createHeaderAndSetValue(10, new byte[4]);
		Assert.assertEquals(CrsAlgorithm.Null, header.getCrsAlgorithm());
	}

	private KeePassHeader createHeaderAndSetValue(int headerId, byte[] value) {
		KeePassHeader keepassHeader = new KeePassHeader();
		keepassHeader.setValue(headerId, value);
		return keepassHeader;
	}
}
