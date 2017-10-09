package de.slackspace.openkeepass.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void whenInputIsStringListShouldJoinStrings() {
        List<String> list = Arrays.asList("a", "b", "c");

        String result = StringUtils.join(list, ";");

        assertThat(result, is("a;b;c"));
    }

    @Test
    public void whenSeparatorInputIsIncompleteShouldJoinStrings() {
        List<String> list = Arrays.asList("a", "b", "");

        String result = StringUtils.join(list, ",");

        assertThat(result, is("a,b,"));
    }

    @Test
    public void whenInputIsNullShouldReturnEmptyString() {
        String result = StringUtils.join(null, ";");

        assertThat(result, is(""));
    }
}
