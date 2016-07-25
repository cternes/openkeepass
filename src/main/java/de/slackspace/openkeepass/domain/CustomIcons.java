package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Represents a list of custom icons in the metadata of a KeePass file.
 *
 */
@Root(strict = false)
public class CustomIcons {

    @ElementList(name = "Icon", inline = true, required = false)
    private List<CustomIcon> customIconList = new ArrayList<CustomIcon>();

    CustomIcons() {
    }

    public CustomIcons(CustomIconsContract customIconsContract) {
        this.customIconList = customIconsContract.getCustomIcons();
    }

    /**
     * Returns all custom icons found inside the database.
     *
     * @return a list of custom icons
     */
    public List<CustomIcon> getIcons() {
        return customIconList;
    }

    /**
     * Retrieves a custom icon based on its uuid.
     *
     * @param uuid
     *            the uuid which should be searched
     * @return the custom icon if found, null otherwise
     */
    public CustomIcon getIconByUuid(UUID uuid) {
        for (CustomIcon customIcon : customIconList) {
            if (customIcon.getUuid() != null && customIcon.getUuid().compareTo(uuid) == 0) {
                return customIcon;
            }
        }

        return null;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customIconList == null) ? 0 : customIconList.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CustomIcons))
            return false;
        CustomIcons other = (CustomIcons) obj;
        if (customIconList == null) {
            if (other.customIconList != null)
                return false;
        } else if (!customIconList.equals(other.customIconList))
            return false;
        return true;
    }

}
