package de.slackspace.openkeepass.util;

public class ResourceUtils {

    public static String getResource(String path) {
        return ResourceUtils.class.getClassLoader().getResource(path).getPath();
    }
}
