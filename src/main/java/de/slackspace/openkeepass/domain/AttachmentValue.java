package de.slackspace.openkeepass.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Represents the value part of an attachment.
 *
 */
@Root(name = "Value", strict = false)
public class AttachmentValue {

    @Attribute(name = "Ref")
    private int ref;

    AttachmentValue() {
    }

    public AttachmentValue(int ref) {
        this.ref = ref;
    }

    public int getRef() {
        return ref;
    }

    @Override
    public String toString() {
        return "AttachmentValue [ref=" + ref + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ref;
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
        AttachmentValue other = (AttachmentValue) obj;
        return ref == other.ref;
    }

}
