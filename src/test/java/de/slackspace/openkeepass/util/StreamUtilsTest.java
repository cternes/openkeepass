package de.slackspace.openkeepass.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

public class StreamUtilsTest {

    @Test
    public void shouldReadThreeBytes() throws IOException {
        byte[] bytes = ByteUtils.hexStringToByteArray("af66b1");
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

        byte[] buffer = new byte[3];
        int read = StreamUtils.read(stream, buffer);

        assertThat(read, is(3));
        assertThat(buffer.length, is(3));
        assertThat(ByteUtils.toHexString(buffer), is("af66b1"));
    }

    @Test
    public void shouldNotCrashOnNullBufferInput() throws IOException {
        byte[] bytes = ByteUtils.hexStringToByteArray("af66b1");
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

        int read = StreamUtils.read(stream, null);
        assertThat(read, is(-1));
    }

    @Test
    public void shouldNotCrashOnNullStreamInput() throws IOException {
        int read = StreamUtils.read(null, new byte[3]);
        assertThat(read, is(-1));
    }
}
