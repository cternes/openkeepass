package de.slackspace.openkeepass.domain.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class is a JAXB adapter to transform boolean values to/from xml using
 * JAXB.
 *
 */
public class BooleanXmlAdapter extends XmlAdapter<String, Boolean> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(Boolean value) throws Exception {
        if (value != null && value) {
            return "True";
        }
        return "False";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Boolean unmarshal(String value) throws Exception {
        return "true".equalsIgnoreCase(value);
    }
}
