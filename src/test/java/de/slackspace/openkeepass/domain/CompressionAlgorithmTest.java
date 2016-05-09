package de.slackspace.openkeepass.domain;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class CompressionAlgorithmTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldParseValueAndThrowException() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Value 1324 is not a valid CompressionAlgorithm");

        CompressionAlgorithm.parseValue(1324);
    }

    @Test
    public void shouldParseValue() {
        CompressionAlgorithm compressionAlgorithm = CompressionAlgorithm.parseValue(1);
        Assert.assertEquals(CompressionAlgorithm.Gzip, compressionAlgorithm);
    }

    @Test
    public void shouldGetIntValue() {
        int intValue = CompressionAlgorithm.getIntValue(CompressionAlgorithm.Gzip);
        Assert.assertEquals(1, intValue);
    }
}
