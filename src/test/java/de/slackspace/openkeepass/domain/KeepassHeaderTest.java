package de.slackspace.openkeepass.domain;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.crypto.RandomGenerator;
import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.util.ResourceUtils;
import de.slackspace.openkeepass.util.StreamUtils;

public class KeepassHeaderTest {

    @Test(expected = IllegalArgumentException.class)
    public void whenCipherIsSetToInvalidValueShouldThrowException() {
        createHeaderAndSetValue(2, new byte[1]);
    }

    @Test
    public void whenCipherIsSetToByteArrayShouldReturnHeaderWithCipher() {
        KeePassHeader header = createHeaderAndSetValue(2, ByteUtils.hexStringToByteArray("31C1F2E6BF714350BE5805216AFC5AFF"));
        Assert.assertTrue("Cipher must be 31C1F2E6BF714350BE5805216AFC5AFF",
                Arrays.equals(ByteUtils.hexStringToByteArray("31C1F2E6BF714350BE5805216AFC5AFF"), header.getCipher()));
    }

    @Test(expected = BufferUnderflowException.class)
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

    @Test
    public void whenGetBytesIsCalledShouldReturnHeaderBytesCorrectly() {
        KeePassHeader header = new KeePassHeader();
        header.setValue(KeePassHeader.MASTER_SEED, ByteUtils.hexStringToByteArray("35ac8b529bc4f6e44194bccd0537fcb433a30bcb847e63156262c4df99c528ca"));
        header.setValue(KeePassHeader.TRANSFORM_SEED, ByteUtils.hexStringToByteArray("0d52d93efc5493ae6623f0d5d69bb76bd976bb717f4ee67abbe43528ebfbb646"));
        header.setTransformRounds(8000);
        header.setValue(KeePassHeader.ENCRYPTION_IV, ByteUtils.hexStringToByteArray("2c605455f181fbc9462aefb817852b37"));
        header.setValue(KeePassHeader.STREAM_START_BYTES, ByteUtils.hexStringToByteArray("69d788d9b01ea1facd1c0bf0187e7d74e4aa07b20d464f3d23d0b2dc2f059ff8"));
        header.setValue(KeePassHeader.CIPHER, ByteUtils.hexStringToByteArray("31C1F2E6BF714350BE5805216AFC5AFF"));
        header.setValue(KeePassHeader.PROTECTED_STREAM_KEY, ByteUtils.hexStringToByteArray("ec77a2169769734c5d26e5341401f8d7b11052058f8455d314879075d0b7e257"));
        header.setCompression(CompressionAlgorithm.Gzip);
        header.setCrsAlgorithm(CrsAlgorithm.Salsa20);

        Assert.assertEquals(
                "03d9a29a67fb4bb50000030002100031c1f2e6bf714350be5805216afc5aff0304000100000004200035ac8b529bc4f6e44194bccd0537fcb433a30bcb847e63156262c4df99c528ca0520000d52d93efc5493ae6623f0d5d69bb76bd976bb717f4ee67abbe43528ebfbb646060800401f0000000000000710002c605455f181fbc9462aefb817852b37082000ec77a2169769734c5d26e5341401f8d7b11052058f8455d314879075d0b7e25709200069d788d9b01ea1facd1c0bf0187e7d74e4aa07b20d464f3d23d0b2dc2f059ff80a0400020000000004000d0a0d0a",
                ByteUtils.toHexString(header.getBytes()));
        Assert.assertEquals(210, header.getHeaderSize());
    }

    @Test
    public void shouldInitializeCryptoValues() {
        KeePassHeader header = new KeePassHeader(new RandomGenerator());

        Assert.assertEquals(32, header.getMasterSeed().length);
        Assert.assertEquals(32, header.getTransformSeed().length);
        Assert.assertEquals(16, header.getEncryptionIV().length);
        Assert.assertEquals(32, header.getProtectedStreamKey().length);
        Assert.assertEquals(CrsAlgorithm.Salsa20, header.getCrsAlgorithm());
        Assert.assertEquals(32, header.getStreamStartBytes().length);
        Assert.assertEquals(CompressionAlgorithm.Gzip, header.getCompression());
        Assert.assertEquals(8000, header.getTransformRounds());
        Assert.assertEquals("31c1f2e6bf714350be5805216afc5aff", ByteUtils.toHexString(header.getCipher()));
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void whenVersionIsNotSupportedShouldThrowException() throws IOException {
        KeePassHeader header = new KeePassHeader(new RandomGenerator());
        
        // unsupported format --> e.g. v5
        byte[] rawHeader = ByteUtils.hexStringToByteArray("03D9A29A67FB4BB501000500");
        
        header.checkVersionSupport(rawHeader);
    }
    
    @Test
    public void whenKdfParameterAreProvidedShouldReadKdfParameters() throws IOException {
        KeePassHeader header = new KeePassHeader(new RandomGenerator());
        FileInputStream fileInputStream = new FileInputStream(ResourceUtils.getResource("DatabaseWithV4Format.kdbx"));
        byte[] rawFile = StreamUtils.toByteArray(fileInputStream);
        
        header.checkVersionSupport(rawFile);
        header.read(rawFile);
        
        VariantDictionary kdfParameters = header.getKdfParameters();
        Assert.assertArrayEquals(ByteUtils.hexStringToByteArray("ef636ddf8c29444b91f7a9a403e30a0c"), kdfParameters.getByteArray("$UUID"));
        Assert.assertEquals(19, kdfParameters.getInt("V"));
        Assert.assertEquals(2, kdfParameters.getLong("I"));
        Assert.assertEquals(2, kdfParameters.getInt("P"));
        Assert.assertEquals(1048576, kdfParameters.getLong("M"));
        Assert.assertArrayEquals(ByteUtils.hexStringToByteArray("7ea16ccbf5f48cb5f77b01a9192123164c5f5f5245a10e5f9c848f47f0c93a4c"), kdfParameters.getByteArray("S"));
    }
    
}
