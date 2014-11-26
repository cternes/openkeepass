package de.slackspace.openkeepass.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.slackspace.openkeepass.crypto.ProtectedStringCrypto;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Group implements KeePassFileElement {

	@XmlTransient
	private KeePassFileElement parent;
	
	@XmlElement(name = "UUID")
	private String uuid;
	
	@XmlElement(name = "Name")
	private String name;
	
	@XmlElement(name = "Group")
	private List<Group> groups;
	
	@XmlElement(name = "Entry")
	private List<Entry> entries;
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Group> getGroups() {
		return groups;
	}
	
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Entry> getEntries() {
		return entries;
	}

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
	public ProtectedStringCrypto getProtectedStringCrypto() {
		return parent.getProtectedStringCrypto();
	}
	
}
