package de.slackspace.openkeepass.util;

public class StringUtils {
    
    private StringUtils() {
    }
    
    public static String join(String[] array, String separator) {
        if (array == null || array.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(array[0]);
        
        for (int i = 1; i < array.length; i++) {
            sb.append(separator).append(array[i]);
        }
        
        return sb.toString();
    }
    
}
