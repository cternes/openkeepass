package de.slackspace.openkeepass.util;

import java.util.List;

public final class StringUtils {

    private StringUtils() {}

    public static String join(List<String> list, String separator) {
        if (list == null || list.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(list.get(0));

        for (int i = 1; i < list.size(); i++) {
            sb.append(separator).append(list.get(i));
        }

        return sb.toString();
    }

}
