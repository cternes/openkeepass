package de.slackspace.openkeepass.domain.xml.adapter;

import org.simpleframework.xml.transform.Transform;

public class BooleanSimpleXmlAdapter implements Transform<Boolean> {

    @Override
    public Boolean read(String arg0) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String write(Boolean value) throws Exception {
        if (value != null && value) {
            return "True";
        }
        return "False";
    }

}
