package de.slackspace.openkeepass.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomIcons {
  @XmlElement(name = "Icon")
  private List<CustomIcon> customIcons = new ArrayList<CustomIcon>();

  /**
   * Returns all custom custom icons found inside the database.
   *
   * @return all custom custom icons
   */
  public List<CustomIcon> getCustomIcons() {
    return customIcons;
  }

  /**
   * Retrieves a custom icon based on its UUID.
   *
   * @param uuid the uuid which should be searched
   * @return the found custom icon or null
   */
  public CustomIcon getCustomIconByUuid(final UUID uuid) {
    for (CustomIcon customIcon : customIcons) {
      if (customIcon.getUuid() != null && customIcon.getUuid().compareTo(uuid) == 0) {
        return customIcon;
      }
    }

    return null;
  }
}
