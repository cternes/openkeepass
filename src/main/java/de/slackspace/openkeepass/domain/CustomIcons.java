package de.slackspace.openkeepass.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a list of custom icons in the metadata of a KeePass file.
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomIcons {

	@XmlElement(name = "Icon")
	private List<CustomIcon> customIcons = new ArrayList<CustomIcon>();

	/**
	 * Returns all custom icons found inside the database.
	 *
	 * @return a list of custom icons
	 */
	public List<CustomIcon> getIcons() {
		return customIcons;
	}

	/**
	 * Retrieves a custom icon based on its uuid.
	 *
	 * @param uuid
	 *            the uuid which should be searched
	 * @return the custom icon if found, null otherwise
	 */
	public CustomIcon getIconByUuid(UUID uuid) {
		for (CustomIcon customIcon : customIcons) {
			if (customIcon.getUuid() != null && customIcon.getUuid().compareTo(uuid) == 0) {
				return customIcon;
			}
		}

		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customIcons == null) ? 0 : customIcons.hashCode());
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
		CustomIcons other = (CustomIcons) obj;
		if (customIcons == null) {
			if (other.customIcons != null)
				return false;
		} else if (!customIcons.equals(other.customIcons))
			return false;
		return true;
	}

}
