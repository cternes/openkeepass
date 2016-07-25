package de.slackspace.openkeepass.domain.xml.adapter;

import org.simpleframework.xml.transform.Transform;

public class BooleanSimpleXmlAdapter implements Transform<Boolean> {

    @Override
    public Boolean read(String value) throws Exception {
        return "true".equalsIgnoreCase(value);
    }

    @Override
    public String write(Boolean value) throws Exception {
        if (value != null && value) {
            return "True";
        }
        return "False";
    }

}
