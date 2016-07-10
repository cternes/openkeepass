package de.slackspace.openkeepass.domain;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;

import de.slackspace.openkeepass.domain.xml.adapter.BooleanSimpleXmlAdapter;

public class PropertyValueTest {

    @Test
    public void shouldMarshallObjectToXml() throws Exception {
        PropertyValue propertyValue = new PropertyValue(false, "TestValue");
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        RegistryMatcher matcher = new RegistryMatcher();
        matcher.bind(Boolean.class, BooleanSimpleXmlAdapter.class);
        Serializer serializer = new Persister(matcher);
        serializer.write(propertyValue, bos);
        
        String xml = cleanXmlString(new String(bos.toByteArray()));
        Assert.assertEquals("<propertyValue Protected='False'>TestValue</propertyValue>", xml);
        
        System.out.println(xml);
    }
    
    private String cleanXmlString(String xml) {
        String xmlRemovedLineBreaks = removeLineBreaks(xml);
        return replaceQuotationMarks(xmlRemovedLineBreaks);
    }
    
    private String removeLineBreaks(String xml) {
        return xml.replaceAll("\n", "");
    }
    
    private String replaceQuotationMarks(String xml) {
        return xml.replaceAll("\"", "'");
    }
}
