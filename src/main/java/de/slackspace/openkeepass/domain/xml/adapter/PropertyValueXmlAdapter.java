package de.slackspace.openkeepass.domain.xml.adapter;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import de.slackspace.openkeepass.domain.PropertyValue;
import de.slackspace.openkeepass.processor.ProtectionStrategy;

public class PropertyValueXmlAdapter implements Converter<PropertyValue> {

    private ProtectionStrategy strategy;

    public PropertyValueXmlAdapter(ProtectionStrategy protectionStrategy) {
        this.strategy = protectionStrategy;
    }

    @Override
    public PropertyValue read(InputNode node) throws Exception {
        String value = node.getValue();

        if (value == null) {
            return new PropertyValue(false, "");
        }

        if (isProtected(node)) {
            String rawValue = strategy.apply(value);
            return new PropertyValue(true, rawValue);
        }
        
        return new PropertyValue(false, value);
    }

    private boolean isProtected(InputNode node) throws Exception {
        InputNode isProtectedNode = node.getAttribute("Protected");
        if (isProtectedNode == null) {
            return false;
        }

        String value = isProtectedNode.getValue();
        return value.equalsIgnoreCase("true");
    }

    @Override
    public void write(OutputNode node, PropertyValue value) throws Exception {
        // not called
    }

}
