package de.slackspace.openkeepass.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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

    @Test
    public void whenInputIsRawUUIDShouldReturnValidUUID() {
        String uuid = StringUtils.convertToUUIDString("28A8836D5642534BB46B34F87E078729");

        assertThat(uuid, is("28A8836D-5642-534B-B46B-34F87E078729"));
    }

    @Test
    public void whenInputIsNullShouldReturnNull() {
        String uuid = StringUtils.convertToUUIDString(null);

        assertThat(uuid, is(nullValue()));
    }

    @Test
    public void whenInputIsNotAtCorrectLengthShouldReturnInputValue() {
        String uuid = StringUtils.convertToUUIDString("abc");

        assertThat(uuid, is("abc"));
    }
}
