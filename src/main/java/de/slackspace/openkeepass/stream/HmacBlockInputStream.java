package de.slackspace.openkeepass.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import de.slackspace.openkeepass.crypto.HmacSha256;
import de.slackspace.openkeepass.crypto.sha.Sha512;
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException;
import de.slackspace.openkeepass.util.ByteUtils;


public class HmacBlockInputStream extends InputStream {

    private static final int EOF = -1;

    private LittleEndianDataInputStream outerStream;
    private InputStream blockStream;
    private int blockNumber = 0;
    private boolean isFinished;
    private byte[] key;

    public HmacBlockInputStream(byte[] key, InputStream inputStream) throws IOException {
        this.key = key;
        this.outerStream = new LittleEndianDataInputStream(inputStream);
        prepareNextBlock();
    }

    private boolean prepareNextBlock() throws IOException {
        byte[] hmac = readHmac();
        int blockSize = readBlockSize();
        byte[] block = readBlock(blockSize);
        byte[] calculatedHmac = calculateHmac(block, blockNumber);
        
        if (!Arrays.equals(hmac, calculatedHmac)) {
            throw new KeePassDatabaseUnreadableException(
                    String.format("Could not verify HMAC for block '%s'", blockNumber));
        }

        blockStream = new ByteArrayInputStream(block);
        blockNumber = blockNumber + 1;

        return isFinished;
    }

    private byte[] calculateHmac(byte[] block, long blockNumber) {
        byte[] blockNumberBytes = ByteUtils.longAsBytes(blockNumber);

        byte[] hashedBlockNumber = Sha512.getInstance().update(blockNumberBytes)
                .hash(key);

        byte[] blockLengthBytes = ByteUtils.intAsBytes(block.length);
        return HmacSha256.getInstance(hashedBlockNumber)
                .update(blockNumberBytes)
                .update(blockLengthBytes)
                .doFinal(block);
    }

    private byte[] readBlock(int blockSize) throws IOException {
        byte[] block = new byte[blockSize];
        outerStream.readFully(block);

        return block;
    }

    private int readBlockSize() throws IOException {
        int blockSize = outerStream.readInt();
        if (blockSize == 0) {
            isFinished = true;
        }

        return blockSize;
    }

    private byte[] readHmac() throws IOException {
        byte[] hmac = new byte[32];
        outerStream.readFully(hmac);

        return hmac;
    }

    @Override
    public int read() throws IOException {
        if (isFinished) {
            return EOF;
        }

        int bytesRead = blockStream.read();

        if (bytesRead == EOF) {
            boolean successful = prepareNextBlock();
            if (successful) {
                bytesRead = blockStream.read();
            }
        }

        return bytesRead;
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return read(bytes, 0, bytes.length);
    }

    @Override
    public int read(byte[] bytes, int offset, int length) throws IOException {
        if (isFinished) {
            return EOF;
        }

        int bytesRead = blockStream.read(bytes, offset, length);

        if (blockStream.available() == 0) {
            prepareNextBlock();
        }

        return bytesRead;
    }
}
