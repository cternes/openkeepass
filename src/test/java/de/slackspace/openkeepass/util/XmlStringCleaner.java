package de.slackspace.openkeepass.util;

public final class XmlStringCleaner {

    public static String cleanXmlString(String xml) {
        String xmlRemovedLineBreaks = removeLineBreaks(xml);
        return replaceQuotationMarks(xmlRemovedLineBreaks);
    }
    
    private static String removeLineBreaks(String xml) {
        return xml.replaceAll("\n", "");
    }
    
    private static String replaceQuotationMarks(String xml) {
        return xml.replaceAll("\"", "'");
    }
}
