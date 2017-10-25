package de.slackspace.openkeepass.domain;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Represents an attachment of an {@link Entry}.
 *
 */
@Root(strict = false, name = "Binary")
public class Attachment implements KeePassFileElement {

    @Element(name = "Key")
    private String key;

    @Element(name = "Value")
    private AttachmentValue attachmentValue;

    private transient byte[] data;

    Attachment() {
    }

    public Attachment(String key, int ref) {
        this.key = key;
        this.attachmentValue = new AttachmentValue(ref);
    }

    public Attachment(String key, int ref, byte[] data) {
        this.key = key;
        this.attachmentValue = new AttachmentValue(ref);
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public int getRef() {
        return attachmentValue.getRef();
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Property [key=" + key + ", attachmentValue=" + attachmentValue + "]";
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((attachmentValue == null) ? 0 : attachmentValue.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Attachment))
            return false;
        Attachment other = (Attachment) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (attachmentValue == null) {
            if (other.attachmentValue != null)
                return false;
        } else if (!attachmentValue.equals(other.attachmentValue))
            return false;
        return true;
    }

}
