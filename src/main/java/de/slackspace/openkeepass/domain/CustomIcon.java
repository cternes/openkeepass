package de.slackspace.openkeepass.domain;

import de.slackspace.openkeepass.xml.UUIDXmlAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.UUID;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomIcon {
  @XmlElement(name = "UUID")
  @XmlJavaTypeAdapter(UUIDXmlAdapter.class)
  private UUID uuid;

  @XmlElement(name = "Data")
  private byte[] data;

  /**
   * Returns the UUID of this custom icon.
   *
   * @return this icon's UUID
   */
  public UUID getUuid() {
    return uuid;
  }

  /**
   * Returns the raw image data as bytes.
   *
   * @return image data in PNG format
   */
  public byte[] getData() {
    return data;
  }
}
