package de.slackspace.openkeepass.domain;

import de.slackspace.openkeepass.xml.UUIDXmlAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.UUID;

/**
 * Represents a custom icon in the KeePass database.
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomIcon {
	
	@XmlElement(name = "UUID")
	@XmlJavaTypeAdapter(UUIDXmlAdapter.class)
	private UUID uuid;

	@XmlElement(name = "Data")
	private byte[] data;

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
}
