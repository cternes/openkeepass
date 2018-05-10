package de.slackspace.openkeepass.domain;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.crypto.RandomGenerator;
import de.slackspace.openkeepass.crypto.sha.Sha256;
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
    public void whenGetBytesIsCalledShouldReturnHeaderBytesCorrectly() throws IOException {
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

        header.checkVersionSupport(header.getBytes());
        header.read(header.getBytes());
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
        // arrange
        KeePassHeader header = new KeePassHeader(new RandomGenerator());
        
        // act
        // unsupported format --> e.g. v5
        byte[] rawHeader = ByteUtils.hexStringToByteArray("03D9A29A67FB4BB501000500");
        
        header.checkVersionSupport(rawHeader);
    }
    
    @Test
    public void whenKdfParameterAreProvidedShouldReadKdfParameters() throws IOException {
        // arrange
        KeePassHeader header = new KeePassHeader(new RandomGenerator());
        FileInputStream fileInputStream = new FileInputStream(ResourceUtils.getResource("DatabaseWithV4Format.kdbx"));
        byte[] rawFile = StreamUtils.toByteArray(fileInputStream);
        
        // act
        header.checkVersionSupport(rawFile);
        header.read(rawFile);
        
        // assert
        KdfDictionary kdfParameters = header.getKdfParameters();
        assertThat(kdfParameters.getUUID(), is(ByteUtils.hexStringToByteArray("ef636ddf8c29444b91f7a9a403e30a0c")));
        assertThat(kdfParameters.getVersion(), is(19));
        assertThat(kdfParameters.getIterations(), is(2L));
        assertThat(kdfParameters.getParallelism(), is(2));
        assertThat(kdfParameters.getMemory(), is(1048576L));
        assertThat(kdfParameters.getSalt(),
                is(ByteUtils.hexStringToByteArray("7ea16ccbf5f48cb5f77b01a9192123164c5f5f5245a10e5f9c848f47f0c93a4c")));
    }
    
    @Test
    public void whenV4FormatIsUsedShouldProvideHMacKey() throws IOException {
        // arrange
        KeePassHeader header = new KeePassHeader();
        FileInputStream fileInputStream = new FileInputStream(ResourceUtils.getResource("DatabaseWithV4Format.kdbx"));
        byte[] rawFile = StreamUtils.toByteArray(fileInputStream);

        header.checkVersionSupport(rawFile);
        header.read(rawFile);

        byte[] hashedPassword = Sha256.getInstance().hash("123");

        // act
        byte[] hmac = header.getHMACKey(hashedPassword);

        // assert
        assertThat(hmac, is(ByteUtils.hexStringToByteArray(
                "46cd61eead3beaf0df1085e2e7b76e969d137e4b7510b86cf144b91129c108391e68100600b59a230e0f2e95ce34decdb0982ce515a822840ad17eadbc1e6220")));
    }

    @Test
    public void whenV4FormatIsUsedShouldReadInnerHeader() throws IOException {
        // arrange
        KeePassHeader header = new KeePassHeader();

        byte[] innerHeader = ByteUtils.hexStringToByteArray(
                "0104000000030000000240000000b231d0a8152619ea18f8baaa850d168918d99402b7e73be403a9317110ae871859841d78d50352d6c6aabd8c4cfc751308da2d6d9e060ec70e1d4afdd2871dd70000000000");

        // act
        header.readInner(innerHeader);

        // assert
        assertThat(header.getCrsAlgorithm(), is(CrsAlgorithm.ChaCha20));
        assertThat(header.getProtectedStreamKey(), is(ByteUtils.hexStringToByteArray(
                "B231D0A8152619EA18F8BAAA850D168918D99402B7E73BE403A9317110AE871859841D78D50352D6C6AABD8C4CFC751308DA2D6D9E060EC70E1D4AFDD2871DD7")));
        assertThat(header.getInnerHeaderSize(), is(83));
    }
}
