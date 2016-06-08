package de.slackspace.openkeepass.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a key value pair of an {@link Entry}. All properties like username
 * or password of an Entry are represented with this class.
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Property implements KeePassFileElement, Cloneable{

    @XmlElement(name = "Key")
    private String key;

    @XmlElement(name = "Value")
    private PropertyValue propertyValue;

    Property() {
    }

    public Property(String key, String value, boolean isProtected) {
        this.key = key;
        this.propertyValue = new PropertyValue(isProtected, value);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return propertyValue.getValue();
    }

    public boolean isProtected() {
        return propertyValue.isProtected();
    }

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    @Override
    public String toString() {
        return "Property [key=" + key + ", propertyValue=" + propertyValue + "]";
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((propertyValue == null) ? 0 : propertyValue.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Property))
            return false;
        Property other = (Property) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (propertyValue == null) {
            if (other.propertyValue != null)
                return false;
        } else if (!propertyValue.equals(other.propertyValue))
            return false;
        return true;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
    	return new Property(this.getKey(), this.getValue(), this.isProtected());
    }

}
