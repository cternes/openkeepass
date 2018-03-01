package de.slackspace.openkeepass.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

public class ByteUtilsTest {

    @Test
    public void shouldConvertUUIDToBytesAndBackAgain() {
        UUID uuid = UUID.randomUUID();

        byte[] bytes = ByteUtils.uuidToBytes(uuid);
        UUID uuid2 = ByteUtils.bytesToUUID(bytes);

        assertThat(uuid, is(uuid2));
    }

    @Test
    public void shouldReadInt() throws IOException {
        int i = 200;
        byte[] bytes = new byte[] { (byte) i };
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        int readInt = ByteUtils.readInt(inputStream);

        assertThat(200, is(readInt));
    }
}
