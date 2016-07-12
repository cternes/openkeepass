package de.slackspace.openkeepass.util;

public final class XmlStringCleaner {

    public static String cleanXmlString(String xml) {
        return replaceQuotationMarks(removeWhitespace(removeLineBreaks(xml)));
    }
    
    private static String removeLineBreaks(String xml) {
        return xml.replaceAll("\n", "");
    }
    
    private static String replaceQuotationMarks(String xml) {
        return xml.replaceAll("\"", "'");
    }

    private static String removeWhitespace(String xml) {
        return xml.replaceAll(">\\s*<", "><");
    }
}
