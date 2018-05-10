package de.slackspace.openkeepass.processor;

public class NullProtectionStrategy implements ProtectionStrategy {

    @Override
    public String apply(String value) {
        return value;
    }

}
