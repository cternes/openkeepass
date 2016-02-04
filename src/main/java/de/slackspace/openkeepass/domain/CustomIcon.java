package de.slackspace.openkeepass.domain;

import de.slackspace.openkeepass.xml.UUIDXmlAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Arrays;
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

	CustomIcon() {
	}

	public CustomIcon(CustomIconBuilder customIconBuilder) {
		this.uuid = customIconBuilder.uuid;
		this.data = customIconBuilder.data;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
