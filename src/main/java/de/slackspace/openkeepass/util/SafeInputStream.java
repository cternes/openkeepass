package de.slackspace.openkeepass.util;

import java.io.IOException;
import java.io.InputStream;

public class SafeInputStream extends InputStream {

    private InputStream inputStream;

    public SafeInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Reads some number of bytes from the underlying input stream and stores
     * them into the buffer array buf. Will throw an exception if no bytes could
     * be read from underlying stream.
     * 
     * @param buf
     *            the buffer into which the data is read.
     * @throws IOException
     *             if no bytes could be read from underlying stream
     * 
     */
    public void readSafe(byte[] buf) throws IOException {
        int readBytes = inputStream.read(buf);

        if (readBytes == -1) {
            throw new IOException("Could not read any bytes from stream");
        }
    }

    /**
     * Skips over and discards numBytes bytes of data from the underlying input
     * stream. Will throw an exception if the given number of bytes could not be
     * skipped.
     * 
     * @param numBytes
     *            the number of bytes to skip
     * @throws IOException
     *             if the given number of bytes could not be skipped
     */
    public void skipSafe(long numBytes) throws IOException {
        long skippedBytes = inputStream.skip(numBytes);

        if (skippedBytes == -1) {
            throw new IOException("Could not skip '" + numBytes + "' bytes in stream");
        }
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

}
