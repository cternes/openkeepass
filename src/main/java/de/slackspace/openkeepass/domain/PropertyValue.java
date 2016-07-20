package de.slackspace.openkeepass.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 * Represents the value part of a key value {@link Property}.
 * <p>
 * 
 * A value can be protected or not depending on the database setting. Protected
 * values will be additionally encrypted in the database. Typically values like
 * passwords are protected.
 *
 */
@Root(strict = false)
public class PropertyValue {

    @Attribute(name = "Protected", required = false)
    private Boolean isProtected;

    @Text(required = false)
    private String value = "";

    PropertyValue() {
    }

    public PropertyValue(boolean isProtected, String value) {
        this.isProtected = isProtected;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isProtected() {
        if (isProtected == null) {
            return false;
        }
        return isProtected.booleanValue();
    }

    @Override
    public String toString() {
        return "PropertyValue [value=" + value + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((isProtected == null) ? 0 : isProtected.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PropertyValue other = (PropertyValue) obj;
        if (isProtected == null) {
            if (other.isProtected != null)
                return false;
        } else if (!isProtected.equals(other.isProtected))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
