package de.slackspace.openkeepass.parser;

import java.util.ArrayList;
import java.util.List;

import de.slackspace.openkeepass.util.StringUtils;

public class TagParser {

    private static final String TAG_SEPARATOR = ";";

    public List<String> fromTagString(String tags) {
        if (tags == null) {
            return new ArrayList<String>();
        }

        String[] splittedTags = tags.split(TAG_SEPARATOR);

        List<String> result = new ArrayList<String>();
        if (splittedTags != null) {
            for (String tag : splittedTags) {
                result.add(tag);
            }
        }

        return result;
    }

    public String toTagString(List<String> tags) {
        if (tags == null) {
            return null;
        }

        return StringUtils.join(tags, TAG_SEPARATOR);
    }
}
