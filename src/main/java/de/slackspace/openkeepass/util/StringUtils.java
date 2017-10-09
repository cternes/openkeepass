package de.slackspace.openkeepass.util;

import java.util.List;

public final class StringUtils {

    private StringUtils() {}

    public static String join(List<String> list, String separator) {
        if (list == null || list.size() == 0) {
            return "";
        }

        return String.join(separator, list);
    }

}
