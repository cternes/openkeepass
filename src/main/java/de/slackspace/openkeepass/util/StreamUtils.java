package de.slackspace.openkeepass.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {

    private static final int BUFFER_SIZE = 4096;
    private static final int EOF = -1;

    private StreamUtils() {
    }

    public static int read(InputStream input, byte[] buffer) throws IOException {
        if(input == null || buffer == null) {
            return -1;
        }

        int remaining = buffer.length;
        while (remaining > 0) {
            int location = buffer.length - remaining;
            int count = input.read(buffer, 0 + location, remaining);
            if (EOF == count) { // EOF
                break;
            }
            remaining -= count;
        }
        return buffer.length - remaining;
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copyLarge(input, output, new byte[BUFFER_SIZE]);
        return output.toByteArray();
    }

    private static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
