package de.slackspace.openkeepass.stream;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

public class HashedBlockOutputStreamTest {

    private static Random random = new Random();

    @Test
    public void testBlockAligned() throws IOException {
        testSize(1024, 1024);
    }

    @Test
    public void testOffset() throws IOException {
        testSize(1500, 1024);
    }

    private void testSize(int blockSize, int bufferSize) throws IOException {
        byte[] orig = new byte[blockSize];

        random.nextBytes(orig);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HashedBlockOutputStream hashedBlockOutputStream = new HashedBlockOutputStream(outputStream);
        hashedBlockOutputStream.write(orig);
        hashedBlockOutputStream.close();

        byte[] encoded = outputStream.toByteArray();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(encoded);
        HashedBlockInputStream hashedBlockInputStream = new HashedBlockInputStream(inputStream);

        ByteArrayOutputStream decoded = new ByteArrayOutputStream();
        while (true) {
            byte[] buf = new byte[1024];
            int read = hashedBlockInputStream.read(buf);
            if (read == -1) {
                break;
            }

            decoded.write(buf, 0, read);
        }
        hashedBlockInputStream.close();

        byte[] out = decoded.toByteArray();
        assertArrayEquals(orig, out);
    }
}
