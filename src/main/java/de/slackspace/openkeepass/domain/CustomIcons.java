package de.slackspace.openkeepass.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents custom icons in the metadata of a KeePass file.
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
	public List<CustomIcon> getCustomIcons() {
		return customIcons;
	}

	/**
	 * Retrieves a custom icon based on its uuid.
	 *
	 * @param uuid the uuid which should be searched
	 * @return the custom icon if found, null otherwise
	 */
	public CustomIcon getCustomIconByUuid(UUID uuid) {
		for (CustomIcon customIcon : customIcons) {
			if (customIcon.getUuid() != null && customIcon.getUuid().compareTo(uuid) == 0) {
				return customIcon;
			}
		}

		return null;
	}
}
