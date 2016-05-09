package de.slackspace.openkeepass.domain;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class CrsAlgorithmTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldParseValueAndThrowException() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Value 1324 is not a valid CrsAlgorithm");

        CrsAlgorithm.parseValue(1324);
    }

    @Test
    public void shouldParseValue() {
        CrsAlgorithm crsAlgorithm = CrsAlgorithm.parseValue(1);
        Assert.assertEquals(CrsAlgorithm.ArcFourVariant, crsAlgorithm);
    }

    @Test
    public void shouldGetIntValue() {
        int intValue = CrsAlgorithm.getIntValue(CrsAlgorithm.Salsa20);
        Assert.assertEquals(2, intValue);
    }
}
