package de.slackspace.openkeepass.domain;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class KeyFile {

    @Element(name = "Key")
    private Key key;

    private boolean isXmlFile = true;

    public KeyFile() {
        // needed for JAXB serialization
    }

    public KeyFile(boolean isXmlFile) {
        this.isXmlFile = isXmlFile;
    }
    
    public KeyFile(boolean isXmlFile, Key key) {
        this.isXmlFile = isXmlFile;
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public boolean isXmlFile() {
        return isXmlFile;
    }

}
