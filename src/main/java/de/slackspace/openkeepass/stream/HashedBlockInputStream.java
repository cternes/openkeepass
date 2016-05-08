package de.slackspace.openkeepass.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import de.slackspace.openkeepass.crypto.Sha256;
import de.slackspace.openkeepass.util.ByteUtils;
import de.slackspace.openkeepass.util.StreamUtils;

public class HashedBlockInputStream extends InputStream {

    private static final int EOF = -1;
    private static final String MSG_INVALID_DATA_FORMAT = "Invalid data format";

    private static final int HASH_SIZE = 32;

    private InputStream baseStream;
    private int bufferPos = 0;
    private byte[] buffer = new byte[0];
    private long bufferIndex = 0;
    private boolean atEnd = false;

    public HashedBlockInputStream(InputStream is) {
        baseStream = is;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException {
        if (atEnd) {
            return EOF;
        }

        int remaining = length;
        int bufferOffset = offset;

        while (remaining > 0) {
            // Get more from the source into the buffer
            if (bufferPos == buffer.length && !readHashedBlock()) {
                return length - remaining;
            }

            // Copy from buffer out
            int copyLen = Math.min(buffer.length - bufferPos, remaining);

            System.arraycopy(buffer, bufferPos, b, bufferOffset, copyLen);

            bufferOffset += copyLen;
            bufferPos += copyLen;

            remaining -= copyLen;
        }

        return length;
    }

    private boolean readHashedBlock() throws IOException {
        if (atEnd) {
            return false;
        }

        bufferPos = 0;

        readIndexFromStream();
        bufferIndex++;

        byte[] storedHash = readStoredHashFromStream();

        int bufferSize = readBufferSizeFromStream();

        if (bufferSize == 0) {
            checkHashIsNotEmpty(storedHash);

            atEnd = true;
            buffer = new byte[0];
            return false;
        }

        fillBufferFromStream(bufferSize);
        computeAndCompareHash(storedHash);

        return true;
    }

    private void fillBufferFromStream(int bufferSize) throws IOException {
        buffer = new byte[bufferSize];
        int readBytes = StreamUtils.read(baseStream, buffer);

        if (readBytes == EOF) {
            throw new IOException(MSG_INVALID_DATA_FORMAT);
        }
    }

    private void checkHashIsNotEmpty(byte[] storedHash) throws IOException {
        for (int hash = 0; hash < HASH_SIZE; hash++) {
            if (storedHash[hash] != 0) {
                throw new IOException(MSG_INVALID_DATA_FORMAT);
            }
        }
    }

    private int readBufferSizeFromStream() throws IOException {
        int bufferSize = ByteUtils.readInt(baseStream);
        if (bufferSize < 0) {
            throw new IOException(MSG_INVALID_DATA_FORMAT);
        }

        return bufferSize;
    }

    private void readIndexFromStream() throws IOException {
        long index = ByteUtils.readInt(baseStream);
        if (index != bufferIndex) {
            throw new IOException(MSG_INVALID_DATA_FORMAT);
        }
    }

    private void computeAndCompareHash(byte[] storedHash) throws IOException {
        byte[] computedHash = Sha256.hash(buffer);
        if (computedHash == null || computedHash.length != HASH_SIZE) {
            throw new IOException("Hash wrong size");
        }

        if (!Arrays.equals(storedHash, computedHash)) {
            throw new IOException("Hashes didn't match");
        }
    }

    private byte[] readStoredHashFromStream() throws IOException {
        byte[] storedHash = new byte[32];
        int readBytes = StreamUtils.read(baseStream, storedHash);

        if (readBytes == EOF) {
            throw new IOException(MSG_INVALID_DATA_FORMAT);
        }

        return storedHash;
    }

    @Override
    public long skip(long n) throws IOException {
        return 0;
    }

    @Override
    public int read() throws IOException {
        if (atEnd)
            return EOF;

        if (bufferPos == buffer.length && !readHashedBlock()) {
            return EOF;
        }

        int output = buffer[bufferPos];
        bufferPos++;

        return output;
    }

    @Override
    public void close() throws IOException {
        baseStream.close();
    }
}
