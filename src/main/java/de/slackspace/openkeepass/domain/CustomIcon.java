package de.slackspace.openkeepass.domain;

import java.util.Arrays;
import java.util.UUID;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Represents a custom icon in the KeePass database.
 *
 */
@Root(strict = false, name = "Icon")
public class CustomIcon {

    @Element(name = "UUID")
    private UUID uuid;

    @Element(name = "Data")
    private byte[] data;

    CustomIcon() {
    }

    public CustomIcon(CustomIconContract customIconContract) {
        this.uuid = customIconContract.getUuid();
        this.data = customIconContract.getData();
    }

    /**
     * Returns the uuid of this custom icon.
     *
     * @return the uuid of the icon
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Returns the raw image data as bytes.
     *
     * @return raw image data as bytes
     */
    public byte[] getData() {
        return data;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CustomIcon))
            return false;
        CustomIcon other = (CustomIcon) obj;
        if (!Arrays.equals(data, other.data))
            return false;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

}
