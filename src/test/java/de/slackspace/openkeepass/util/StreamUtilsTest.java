package de.slackspace.openkeepass.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class StreamUtilsTest {

    @Test
    public void shouldReadThreeBytes() throws IOException {
        byte[] bytes = ByteUtils.hexStringToByteArray("af66b1");
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

        byte[] buffer = new byte[3];
        int read = StreamUtils.read(stream, buffer);

        Assert.assertEquals(3, read);
        Assert.assertEquals(3, buffer.length);
        Assert.assertEquals("af66b1", ByteUtils.toHexString(buffer));
    }

    @Test
    public void shouldNotCrashOnNullBufferInput() throws IOException {
        byte[] bytes = ByteUtils.hexStringToByteArray("af66b1");
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

        int read = StreamUtils.read(stream, null);
        Assert.assertEquals(-1, read);
    }

    @Test
    public void shouldNotCrashOnNullStreamInput() throws IOException {
        int read = StreamUtils.read(null, new byte[3]);
        Assert.assertEquals(-1, read);
    }
}
