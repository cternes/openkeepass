package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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

	@XmlTransient
	private KeePassFileElement parent;
	
	@XmlElement(name = "UUID")
	@XmlJavaTypeAdapter(UUIDXmlAdapter.class)
	private String uuid;
	
	@XmlElement(name = "Name")
	private String name;
	
	@XmlElement(name = "IconID")
	private int iconId = 49; 
	
	@XmlElement(name = "Times")
	private Times times;
	
	@XmlElement(name = "IsExpanded")
	private boolean isExpanded;
	
	@XmlElement(name = "Entry")
	private List<Entry> entries = new ArrayList<Entry>();

	@XmlElement(name = "Group")
	private List<Group> groups = new ArrayList<Group>();
	
	public Group() {
		setUuid(UUID.randomUUID().toString());
	}
	
	/**
	 * Retrieves the Uuid of this group.
	 * 
	 * @return the Uuid of this group
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Sets the Uuid of this group.
	 * 
	 * @param uuid the uuid of this group
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
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
	 * Sets the name of the group.
	 * 
	 * @param name the name of the group
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves all subgroups of this group.
	 * 
	 * @return all subgroups of this group
	 */
	public List<Group> getGroups() {
		return groups;
	}
	
	public void setGroups(List<Group> groups) {
		this.groups = groups;
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
	 * Sets the entries of this group.
	 * 
	 * @param entries the entries of this group
	 */
	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	public void setParent(KeePassFileElement element) {
		parent = element;
		
		if(groups != null) {
			for (Group group : groups) {
				group.setParent(this);
			}
		}

		if(entries != null) {
			for (Entry entry : entries) {
				entry.setParent(this);
			}
		}
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
	 * Sets the icon of the group.
	 * 
	 * @param iconId the icon of the group
	 */
	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public Times getTimes() {
		return times;
	}

	public void setTimes(Times times) {
		this.times = times;
	}

	/**
	 * Retrieves the last expanded status of the group.
	 * 
	 * @return true if the group was expanded the last time it was opened in keepass
	 */
	public boolean isExpanded() {
		return isExpanded;
	}

	/**
	 * Sets the expanded status of the group.
	 * 
	 * @param isExpanded true if the group should appear to be expanded the last time it was opened in keepass
	 */
	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}
}
