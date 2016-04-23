package de.slackspace.openkeepass.util;

import static org.junit.Assert.assertEquals;

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

        assertEquals(uuid, uuid2);
    }

    @Test
    public void shouldReadInt() throws IOException {
        int i = 200;
        byte[] bytes = new byte[] { (byte) i };
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        int readInt = ByteUtils.readInt(inputStream);

        assertEquals(200, readInt);
    }
}
