package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.slackspace.openkeepass.util.IconUtils;
import de.slackspace.openkeepass.xml.BooleanXmlAdapter;
import de.slackspace.openkeepass.xml.UUIDXmlAdapter;

/**
 * A Group represents a structure that consists of entries and subgroups.
 * 
 * @see Entry
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Group implements KeePassFileElement {

	@XmlElement(name = "UUID")
	@XmlJavaTypeAdapter(UUIDXmlAdapter.class)
	private UUID uuid;

	@XmlElement(name = "Name")
	private String name;

	@XmlElement(name = "IconID")
	private int iconId = 49;

	@XmlElement(name = "CustomIconUUID")
	@XmlJavaTypeAdapter(UUIDXmlAdapter.class)
	private UUID customIconUUID;

	@XmlElement(name = "Times")
	private Times times;

	@XmlElement(name = "IsExpanded")
	@XmlJavaTypeAdapter(BooleanXmlAdapter.class)
	private Boolean isExpanded;

	@XmlElement(name = "Entry")
	private List<Entry> entries = new ArrayList<Entry>();

	@XmlElement(name = "Group")
	private List<Group> groups = new ArrayList<Group>();

	Group() {
		uuid = UUID.randomUUID();
	}

	public Group(GroupBuilder builder) {
		entries = builder.entries;
		groups = builder.groups;
		iconId = builder.iconId;
		isExpanded = builder.isExpanded;
		name = builder.name;
		times = builder.times;
		uuid = builder.uuid;
	}

	/**
	 * Retrieves the Uuid of this group.
	 * 
	 * @return the Uuid of this group
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Retrieves the name of the group.
	 * 
	 * @return the name of the group
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves all subgroups of this group.
	 * 
	 * @return all subgroups of this group
	 */
	public List<Group> getGroups() {
		return groups;
	}

	/**
	 * Retrieves all entries of this group.
	 * 
	 * @return all entries of this group
	 * @see Entry
	 */
	public List<Entry> getEntries() {
		return entries;
	}

	/**
	 * Retrieves the entry with the given title.
	 * 
	 * @param title the title of the entry which should be retrieved
	 * @return an entry with matching title
	 */
	public Entry getEntryByTitle(String title) {
		for (Entry entry : entries) {
			if(entry.getTitle().equalsIgnoreCase(title)) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Retrieves the icon of this group.
	 * 
	 * @return the icon of this group
	 */
	public int getIconId() {
		return iconId;
	}

	/**
	 * Returns the custom icon of this group.
	 *
	 * @return the UUID of the custom icon or null
	 */
	public UUID getCustomIconUuid() {
		return customIconUUID;
	}

	/**
	 * Returns the raw data of either the custom icon (if specified) or the chosen stock icon.
	 * You need to pass the registry of custom icons to be used, if a custom icon is specified (see {@link Meta#getCustomIcons()}).
	 *
	 * @return raw image data in PNG format or null
	 */
	public byte[] getIconData(CustomIcons customIcons) {
		if (customIconUUID != null) {
			if (customIcons == null) throw new IllegalArgumentException("item uses a custom icon, but customIcons is null");
			return customIcons.getIconByUuid(customIconUUID).getData();
		} else {
			return IconUtils.getStockIconData(iconId);
		}
	}

	public Times getTimes() {
		return times;
	}

	/**
	 * Retrieves the last expanded status of the group.
	 * 
	 * @return true if the group was expanded the last time it was opened in keepass
	 */
	public boolean isExpanded() {
		if(isExpanded == null) {
			return false;
		}
		return isExpanded.booleanValue();
	}

}
